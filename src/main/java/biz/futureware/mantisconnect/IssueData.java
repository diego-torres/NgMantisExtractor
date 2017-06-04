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
package biz.futureware.mantisconnect;

import com.nowgroup.ngMantisExtractor.mbt.dto.Project;
import com.nowgroup.ngMantisExtractor.mbt.dto.User;

/**
 * @author https://github.com/diego-torres
 *
 */
public class IssueData {
	private int id;
	private IssueStatus iStatus = IssueStatus.NEW;
	private IssueResolution iResolution = IssueResolution.OPEN;
	private User handler;
	private User reporter;
	private ViewState viewState = new ViewState(10, "public");
	private Project project;
	private String category;
	private String summary;
	private String description;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the iStatus
	 */
	public IssueStatus getiStatus() {
		return iStatus;
	}

	/**
	 * @param iStatus
	 *            the iStatus to set
	 */
	public void setiStatus(IssueStatus iStatus) {
		this.iStatus = iStatus;
	}

	/**
	 * @return the iResolution
	 */
	public IssueResolution getiResolution() {
		return iResolution;
	}

	/**
	 * @param iResolution the iResolution to set
	 */
	public void setiResolution(IssueResolution iResolution) {
		this.iResolution = iResolution;
	}

	/**
	 * @return the handler
	 */
	public User getHandler() {
		return handler;
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(User handler) {
		this.handler = handler;
	}

	/**
	 * @return the reporter
	 */
	public User getReporter() {
		return reporter;
	}

	/**
	 * @param reporter the reporter to set
	 */
	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the viewState
	 */
	public ViewState getViewState() {
		return viewState;
	}

	/**
	 * @param viewState the viewState to set
	 */
	public void setViewState(ViewState viewState) {
		this.viewState = viewState;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
