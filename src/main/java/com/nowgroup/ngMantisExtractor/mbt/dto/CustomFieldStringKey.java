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

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author https://github.com/diego-torres
 *
 */
@Embeddable
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CustomFieldStringKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private Bug bug;
	private CustomField field;

	/**
	 * @return the bug<
	 */
	@ManyToOne
	@JoinColumn(name = "bug_id")
	@JsonIgnore
	public Bug getBug() {
		return bug;
	}

	/**
	 * @param bug
	 *            the bug to set
	 */
	public void setBug(Bug bug) {
		this.bug = bug;
	}

	/**
	 * @return the field
	 */
	@ManyToOne
	@JoinColumn(name = "field_id")
	public CustomField getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(CustomField field) {
		this.field = field;
	}

}
