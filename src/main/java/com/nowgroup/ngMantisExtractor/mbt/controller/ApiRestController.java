/**
 * Copyright 2016 https://github.com/diego-torres
 *
 * Licensed under the MIT License (MIT).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nowgroup.ngMantisExtractor.mbt.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowgroup.ngMantisExtractor.mbt.dto.Bug;
import com.nowgroup.ngMantisExtractor.mbt.dto.CustomFieldString;
import com.nowgroup.ngMantisExtractor.mbt.dto.User;
import com.nowgroup.ngMantisExtractor.mbt.repo.UserRepository;
import com.nowgroup.ngMantisExtractor.scs.dto.ChangeRequest;
import com.nowgroup.ngMantisExtractor.scs.repo.ChangeRequestRepository;
import com.nowgroup.ngMantisExtractor.scs.repo.PackingListRepository;

import biz.futureware.mantisconnect.IssueData;
import biz.futureware.mantisconnect.IssueStatus;

/**
 * @author https://github.com/diego-torres
 *
 */
@Controller
@RequestMapping(path = "/api")
public class ApiRestController {
	@Autowired
	private BugRestController bugController;

	@Autowired
	private MantisSoapClient soapClient;

	@Autowired
	private UserRepository usersRepository;

	@Autowired
	private PackingListRepository packingListRepository;

	@Autowired
	private ChangeRequestRepository changeRequestRepository;

	@Value("${NgMantisExtractor.bot.username}")
	private String mtbtBotUserName;

	@Value("${NgMantisExtractor.max-retry}")
	private Integer maxRetry;

	private Map<String, Integer> retryMap = new HashMap<>();

	@GetMapping(path = "/new")
	public @ResponseBody String gatherNew() {
		List<Bug> newBugs = bugController.getNewBugs();
		newBugs.forEach(bug -> {
			soapClient.assignIssue(ackIssueData(bug));
			// TODO: Handle fail.
			integrate(bug);
		});
		return "OK";
	}

	@GetMapping(path = "/retry")
	public @ResponseBody String gatherRetry() {
		List<Bug> retryBugs = bugController.getRetryTaggedBugs();
		// TODO: iterate the retry bugs: validate existence and integrate if
		// resolved or else add them to the retry map++
		return "OK";
	}

	public String gatherAssigned() {
		// TODO: Assigned to bot user + feedback status.
		return "OK";
	}

	private void integrate(Bug bug) {
		if (validate(bug)) {
			ChangeRequest cr = new ChangeRequest();
			cr.setBugId(bug.getId());
			cr.setCategory(bug.getCategory().getName());

			CustomFieldString cfPackingList = bug.getCustomFields().stream().filter(cf -> {
				return "packing list".equalsIgnoreCase(cf.getKey().getField().getName());
			}).findFirst().orElse(null);

			if (cfPackingList != null)
				cr.setPackingList(cfPackingList.getValue());

			CustomFieldString cfSalesOrder = bug.getCustomFields().stream().filter(cf -> {
				return "sales order".equalsIgnoreCase(cf.getKey().getField().getName());
			}).findFirst().orElse(null);

			if (cfSalesOrder != null)
				cr.setSalesOrder(cfSalesOrder.getValue());

			if ("change delivery date".equalsIgnoreCase(cr.getCategory())) {
				CustomFieldString cfDeliveryDate = bug.getCustomFields().stream().filter(cf -> {
					return "delivery date".equalsIgnoreCase(cf.getKey().getField().getName());
				}).findFirst().orElse(null);
				cr.setRequestedDeliveryDate(
						new Date(TimeUnit.SECONDS.toMillis(Long.parseLong(cfDeliveryDate.getValue()))));
			}

			if ("hold".equalsIgnoreCase(cr.getCategory()))
				cr.setRequestedLock(true);

			if ("unhold".equalsIgnoreCase(cr.getCategory()))
				cr.setRequestedLock(false);

			// TODO: Split CR to packing list ids.

			changeRequestRepository.save(cr);
			// TODO: Change status to resolved
		}
	}

	private boolean validate(Bug bug) {

		CustomFieldString cfPackingList = bug.getCustomFields().stream().filter(cf -> {
			return "packing list".equalsIgnoreCase(cf.getKey().getField().getName());
		}).findFirst().orElse(null);

		CustomFieldString cfSalesOrder = bug.getCustomFields().stream().filter(cf -> {
			return "sales order".equalsIgnoreCase(cf.getKey().getField().getName());
		}).findFirst().orElse(null);

		if (bug.getTags().isEmpty()) {
			// validate exists in supply_chain
			if (!(StreamSupport.stream(packingListRepository.findAll().spliterator(), false)
					.anyMatch(b -> b.getPackingList().equalsIgnoreCase(cfPackingList.getValue()))
					|| StreamSupport.stream(packingListRepository.findAll().spliterator(), false)
							.anyMatch(b -> b.getSalesOrder().equalsIgnoreCase(cfSalesOrder.getValue())))) {
				System.out.println("annotate and label for retry");

				// label for retry
				soapClient.tagRetry(bug.getId());
				return false;
			}
		}

		// Validate either packing list or sales order is present.
		if ((null == cfPackingList || null == cfPackingList.getValue() || "".equals(cfPackingList.getValue().trim()))
				&& (null == cfSalesOrder || null == cfSalesOrder.getValue()
						|| "".equals(cfSalesOrder.getValue().trim()))) {

			// Add note
			soapClient.addNote(bug.getId(),
					"Either packing list or sales order must be specified for this request to proceed.");
			// change status and reassign to requester.
			soapClient.assignIssue(this.feedbackIssueData(bug));
			// Handle SOAP Failure
			return false;
		}

		if ("change delivery date".equalsIgnoreCase(bug.getCategory().getName())) {
			CustomFieldString cfDeliveryDate = bug.getCustomFields().stream().filter(cf -> {
				return "delivery date".equalsIgnoreCase(cf.getKey().getField().getName());
			}).findFirst().orElse(null);

			// Validate delivery date exists for change delivery date category
			if (null == cfDeliveryDate || null == cfDeliveryDate.getValue()
					|| "".equals(cfDeliveryDate.getValue().trim())) {
				// If fail to include delivery date: change status
				// Add note
				soapClient.addNote(bug.getId(),
						"You must provide a delivery date for a change delivery date request type.");
				// change status and reassign to requester.
				soapClient.assignIssue(this.feedbackIssueData(bug));
				return false;
			}

			Date deliveryDate = new Date(TimeUnit.SECONDS.toMillis(Long.parseLong(cfDeliveryDate.getValue())));
			Date today = new Date();
			// deliveryDate.before(LocalDateTime.from(today.toInstant()).plusDays(2)))

			if (LocalDateTime.from(deliveryDate.toInstant())
					.isBefore(LocalDateTime.from(today.toInstant()).plusDays(2))) {
				// Add note
				soapClient.addNote(bug.getId(),
						"Your request to change delivery date must allow at least 2 days to arrange the change.");
				// change status and reassign to requester.
				soapClient.assignIssue(this.feedbackIssueData(bug));
			}
		}

		return true;
	}

	private IssueData feedbackIssueData(Bug bug) {
		IssueData issueData = new IssueData();
		issueData.setiStatus(IssueStatus.FEEDBACK);

		issueData.setId(bug.getId());
		issueData.setHandler(bug.getReporter());
		issueData.setReporter(bug.getReporter());
		issueData.setProject(bug.getProject());
		issueData.setCategory(bug.getCategory().getName());
		issueData.setSummary(bug.getSummary());
		issueData.setDescription(bug.getBugText().getDescription());

		return issueData;
	}

	private IssueData ackIssueData(Bug bug) {
		IssueData issueData = new IssueData();
		issueData.setiStatus(IssueStatus.ACKNOWLEDGED);

		issueData.setId(bug.getId());
		issueData.setHandler(findUserByName(mtbtBotUserName));
		issueData.setReporter(bug.getReporter());
		issueData.setProject(bug.getProject());
		issueData.setCategory(bug.getCategory().getName());
		issueData.setSummary(bug.getSummary());
		issueData.setDescription(bug.getBugText().getDescription());

		return issueData;
	}

	private User findUserByName(String userName) {
		return StreamSupport.stream(usersRepository.findAll().spliterator(), false).findFirst().orElse(null);
	}
}
