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
package com.nowgroup.ngMantisExtractor.mbt.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * @author https://github.com/diego-torres
 *
 */
@Entity
@Table(name = "mantis_bug_table")
public class Bug implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Category category;
	private Project project;
	private BugText bugText;

	private User reporter;
	private User handler;
	private Integer priority;
	private Integer severity;
	private String summary;
	private Integer submitted;
	private Integer status;
	private Integer resolution;

	private List<Tag> tags = new ArrayList<>();
	private List<CustomFieldString> customFields = new ArrayList<>();

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the category
	 */
	@ManyToOne
	@JoinColumn(name = "category_id")
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @return the project
	 */
	@ManyToOne
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the bugText
	 */
	@ManyToOne
	@JoinColumn(name = "bug_text_id")
	public BugText getBugText() {
		return bugText;
	}

	/**
	 * @param bugText
	 *            the bugText to set
	 */
	public void setBugText(BugText bugText) {
		this.bugText = bugText;
	}

	/**
	 * @return the reporter
	 */
	@ManyToOne
	@JoinColumn(name = "reporter_id")
	public User getReporter() {
		return reporter;
	}

	/**
	 * @param reporter
	 *            the reporter to set
	 */
	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the handler
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "handler_id")
	@NotFound(action = NotFoundAction.IGNORE)
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
	 * @return the priority
	 */
	@Column
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the severity
	 */
	@Column
	public Integer getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	/**
	 * @return the summary
	 */
	@Column
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the submited
	 */
	@Column(name = "date_submitted")
	public Integer getSubmitted() {
		return submitted;
	}

	/**
	 * @param submited
	 *            the submited to set
	 */
	public void setSubmitted(Integer submitted) {
		this.submitted = submitted;
	}

	/**
	 * @return the status
	 */
	@Column
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the resolution
	 */
	@Column
	public Integer getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(Integer resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the tags
	 */
	@ManyToMany
	@JoinTable(name = "mantis_bug_tag_table", joinColumns = @JoinColumn(name = "bug_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the customFields
	 */
	@OneToMany(mappedBy = "key.bug", fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<CustomFieldString> getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields
	 *            the customFields to set
	 */
	public void setCustomFields(List<CustomFieldString> customFields) {
		this.customFields = customFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Bug [id=" + id + ", category=" + category + ", project=" + project + ", bugText=" + bugText
				+ ", reporter=" + reporter + ", handler=" + handler + ", priority=" + priority + ", severity="
				+ severity + ", summary=" + summary + ", submitted=" + submitted + ", status=" + status
				+ ", resolution=" + resolution + ", tags=" + tags + ", customFields=" + customFields + "]";
	}
}
