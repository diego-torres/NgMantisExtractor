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

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowgroup.ngMantisExtractor.mbt.dto.Bug;
import com.nowgroup.ngMantisExtractor.mbt.dto.User;
import com.nowgroup.ngMantisExtractor.mbt.repo.UserRepository;

import biz.futureware.mantisconnect.IssueData;

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

	@Value("${NgMantisExtractor.bot.username}")
	private String mtbtBotUserName;

	@GetMapping(path = "/new")
	public @ResponseBody String gatherNew() {
		List<Bug> newBugs = bugController.getNewBugs();
		newBugs.forEach(bug -> {
			soapClient.ackNAssign(ackIssueData(bug));
			integrate(bug);
		});
		return "OK";
	}

	private void integrate(Bug bug) {
		// TODO: validate exists in supply_chain
		// TODO: If does not exists, create annotation and label for retry.
		// TODO: Store validated to database.
	}

	private IssueData ackIssueData(Bug bug) {
		IssueData issueData = new IssueData();

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
