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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowgroup.ngMantisExtractor.mbt.dto.Bug;
import com.nowgroup.ngMantisExtractor.mbt.repo.BugRepository;

/**
 * @author https://github.com/diego-torres
 *
 */
@Controller
@RequestMapping(path = "/bug")
public class BugRestController {
	@Autowired
	private BugRepository repository;

	@GetMapping(path = "/all")
	public @ResponseBody List<Bug> getAllBugs() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).filter(e -> e.getStatus() < 80)
				.collect(Collectors.toList());
	}

	@GetMapping(path = "/new")
	public @ResponseBody List<Bug> getNewBugs() {
		return getAllBugs().stream().filter(e -> e.getHandler() == null).collect(Collectors.toList());
	}

	@GetMapping(path = "/retry")
	public @ResponseBody List<Bug> getRetryTaggedBugs() {
		return getAllBugs().stream()
				.filter(e -> e.getTags().stream().anyMatch(t -> "retry".equalsIgnoreCase(t.getName())))
				.collect(Collectors.toList());
	}
}
