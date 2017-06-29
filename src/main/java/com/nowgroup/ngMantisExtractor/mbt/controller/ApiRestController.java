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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.nowgroup.ngMantisExtractor.scs.dto.PackingList;
import com.nowgroup.ngMantisExtractor.scs.repo.ChangeRequestRepository;
import com.nowgroup.ngMantisExtractor.scs.repo.PackingListRepository;

import biz.futureware.mantisconnect.IssueData;
import biz.futureware.mantisconnect.IssueResolution;
import biz.futureware.mantisconnect.IssueStatus;

/**
 * @author https://github.com/diego-torres
 *
 */
@Controller
@RequestMapping(path = "/api")
public class ApiRestController {
	private static final Logger logger = LoggerFactory.getLogger(ApiRestController.class);

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
		logger.info("START - Executing gatherNew()");
		String result = "OK";
		List<Bug> newBugs = bugController.getNewBugs();
		for (Bug bug : newBugs) {
			logger.debug("Processing new issue: " + bug);
			String assignment = soapClient.assignIssue(ackIssueData(bug));
			if ("OK".equalsIgnoreCase(assignment)) {
				logger.debug("Issue ready to integrate: " + bug);
				integrate(bug);
			} else {
				logger.warn("failed to assign issue: " + bug);
				result = "FAIL";
				break;
			}
		}
		logger.info("END - Executing gatherNew() with result " + result);
		return result;
	}

	@GetMapping(path = "/retry")
	public @ResponseBody String gatherRetry() {
		List<Bug> retryBugs = bugController.getRetryTaggedBugs();
		for (Bug retryBug : retryBugs) {
			// validate existence
			if (validateExists(retryBug)) {
				// TODO: Handle fail.
				integrate(retryBug);
			} else {
				String mapKey = salesOrderFromBug(retryBug).concat("||").concat(packingListFromBug(retryBug));
				if (retryMap.containsKey(mapKey)) {
					retryMap.replace(mapKey, retryMap.getOrDefault(mapKey, 0) + 1);
					if (retryMap.getOrDefault(mapKey, 0) > maxRetry) {
						retryMap.remove(mapKey);
						rejectIssue(retryBug, "Unable to find sales order || packing list: " + mapKey + ".");
					}
				} else {
					retryMap.put(mapKey, 1);
				}
			}
		}
		return "OK";
	}

	private void integrate(Bug bug) {
		logger.info("START - integrate(" + bug + ")");
		if (validate(bug)) {
			String packingList = packingListFromBug(bug);
			String salesOrder = salesOrderFromBug(bug);

			// Split CR to packing list ids.
			if (null == packingList || "".equals(packingList)) {
				List<Integer> salesOrderPackingLists = packingListIdFromSalesOrder(bug);
				logger.debug("Processing a list of packing lists [" + salesOrderPackingLists.size()
						+ "] based in sales order [" + salesOrder + "].");
				salesOrderPackingLists.forEach(p -> storeChangeRequest(bug, p));
			} else {
				logger.debug("Processing a single packing list[" + packingList + "]");
				// single request for packing list.
				storeChangeRequest(bug, packingListId(bug));
			}
			// Change status to resolved
			soapClient.assignIssue(this.resolveIssueData(bug));
		} else {
			logger.warn("Unable to complete integration, validation failed for: " + bug);
		}
		logger.info("END - integrate(" + bug + ")");
	}

	private void storeChangeRequest(Bug bug, Integer packingListId) {
		logger.info("START - storeChangeRequest(" + bug + ", " + packingListId + ")");
		ChangeRequest cr = new ChangeRequest();
		cr.setBugId(bug.getId());
		cr.setNgPackingListId(packingListId);
		cr.setCategory(bug.getCategory().getName());
		cr.setPackingList(packingListFromBug(bug));
		cr.setSalesOrder(salesOrderFromBug(bug));

		if ("change delivery date".equalsIgnoreCase(cr.getCategory())) {
			CustomFieldString cfDeliveryDate = bug.getCustomFields().stream().filter(cf -> {
				return "delivery date".equalsIgnoreCase(cf.getKey().getField().getName());
			}).findFirst().orElse(null);
			cr.setRequestedDeliveryDate(new Date(TimeUnit.SECONDS.toMillis(Long.parseLong(cfDeliveryDate.getValue()))));
		}

		if ("hold".equalsIgnoreCase(cr.getCategory()))
			cr.setRequestedLock(true);

		if ("unhold".equalsIgnoreCase(cr.getCategory()))
			cr.setRequestedLock(false);

		changeRequestRepository.save(cr);
		logger.info("END - storeChangeRequest(" + bug + ", " + packingListId + ")");
	}

	private boolean validate(Bug bug) {
		String packingList = packingListFromBug(bug);
		String salesOrder = salesOrderFromBug(bug);

		if (bug.getTags().isEmpty()) {
			// validate exists in supply_chain
			if (!validateExists(bug)) {
				String rejectMessage = "Packing List [" + packingList + "] or Sales Order [" + salesOrder
						+ "] not found in Supply Chain Software, please try this change in the internal Oracle Records.";
				rejectIssue(bug, rejectMessage);
				return false;
			}
		}

		// Validate either packing list or sales order is present.
		if ((null == packingList || "".equals(packingList)) && (null == salesOrder || "".equals(salesOrder))) {
			rejectIssue(bug, "Either packing list or sales order must be specified for this request to proceed.");
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
				rejectIssue(bug, "You must provide a delivery date for a change delivery date request type.");
				return false;
			}

			Date deliveryDate = new Date(TimeUnit.SECONDS.toMillis(Long.parseLong(cfDeliveryDate.getValue())));
			Date today = new Date();
			// deliveryDate.before(LocalDateTime.from(today.toInstant()).plusDays(2)))

			if (LocalDate.from(deliveryDate.toInstant().atZone(ZoneId.of("UTC")))
					.isBefore(LocalDate.from(today.toInstant().atZone(ZoneId.of("UTC"))).plusDays(2))) {
				rejectIssue(bug,
						"Your request to change delivery date must allow at least 2 days to arrange the change.");
				return false;
			}
		}

		return true;
	}

	private void rejectIssue(Bug bug, String rejectReason) {
		logger.warn("Rejecting " + bug + " due to the following reason: " + rejectReason);
		// Add note
		soapClient.addNote(bug.getId(), rejectReason);
		// change status and reassign to requester.
		soapClient.assignIssue(this.rejectIssueData(bug));
		// TODO: Handle SOAP failure
	}

	private boolean validateExists(Bug bug) {
		String packingList = packingListFromBug(bug);
		boolean packingListExists = packingListStream().anyMatch(b -> b.getPackingList().equalsIgnoreCase(packingList));
		String salesOrder = salesOrderFromBug(bug);
		boolean salesOrderExists = packingListStream().anyMatch(b -> b.getSalesOrder().equalsIgnoreCase(salesOrder));
		return packingListExists || salesOrderExists;
	}

	private Integer packingListId(Bug bug) {
		String packingListName = packingListFromBug(bug);
		if (null == packingListName)
			return null;
		Stream<PackingList> streamPackingList = packingListStream();

		PackingList packingList = streamPackingList.filter(b -> b.getPackingList().equalsIgnoreCase(packingListName))
				.findFirst().orElse(null);

		if (null == packingList)
			return null;
		else
			return packingList.getPackingListId();
	}

	private List<Integer> packingListIdFromSalesOrder(Bug bug) {
		String salesOrderName = salesOrderFromBug(bug);
		if (null == salesOrderName)
			return null;
		Stream<PackingList> streamPackingList = packingListStream();

		return streamPackingList.filter(b -> salesOrderName.equalsIgnoreCase(b.getSalesOrder()))
				.mapToInt(b -> b.getPackingListId()).boxed().collect(Collectors.toList());
	}

	private Stream<PackingList> packingListStream() {
		Spliterator<PackingList> plSpliterator = packingListRepository.findAll().spliterator();
		return StreamSupport.stream(plSpliterator, false);
	}

	private String packingListFromBug(Bug bug) {
		CustomFieldString cfPackingList = bug.getCustomFields().stream()
				.filter(cf -> "packing list".equalsIgnoreCase(cf.getKey().getField().getName())).findFirst()
				.orElse(null);

		if (null == cfPackingList || null == cfPackingList.getValue())
			return null;
		return cfPackingList.getValue();
	}

	private String salesOrderFromBug(Bug bug) {
		CustomFieldString cfSalesOrder = bug.getCustomFields().stream()
				.filter(cf -> "sales order".equalsIgnoreCase(cf.getKey().getField().getName())).findFirst()
				.orElse(null);
		if (null == cfSalesOrder || null == cfSalesOrder.getValue())
			return null;
		return cfSalesOrder.getValue();
	}

	private IssueData resolveIssueData(Bug bug) {
		IssueData issueData = new IssueData();
		issueData.setiStatus(IssueStatus.RESOLVED);
		issueData.setiResolution(IssueResolution.FIXED);

		issueData.setId(bug.getId());
		issueData.setHandler(findUserByName(mtbtBotUserName));
		issueData.setReporter(bug.getReporter());
		issueData.setProject(bug.getProject());
		issueData.setCategory(bug.getCategory().getName());
		issueData.setSummary(bug.getSummary());
		issueData.setDescription(bug.getBugText().getDescription());

		return issueData;
	}

	private IssueData rejectIssueData(Bug bug) {
		IssueData issueData = new IssueData();
		issueData.setiStatus(IssueStatus.RESOLVED);
		issueData.setiResolution(IssueResolution.WONT_FIX_IT);

		issueData.setId(bug.getId());
		issueData.setHandler(findUserByName(mtbtBotUserName));
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
