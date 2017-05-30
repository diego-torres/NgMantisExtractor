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
package com.nowgroup.ngMantisExtractor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author https://github.com/diego-torres
 *
 */
public class ChangeRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private int bugId;
	private Integer ngPackingListId;
	private String packingList;
	private String salesOrder;
	private Date requestedDeliveryDate;
	private Boolean requestedLock;

	/**
	 * @return the bugId
	 */
	public int getBugId() {
		return bugId;
	}

	/**
	 * @param bugId
	 *            the bugId to set
	 */
	public void setBugId(int bugId) {
		this.bugId = bugId;
	}

	/**
	 * @return the packingList
	 */
	public String getPackingList() {
		return packingList;
	}

	/**
	 * @param packingList
	 *            the packingList to set
	 */
	public void setPackingList(String packingList) {
		this.packingList = packingList;
	}

	/**
	 * @return the salesOrder
	 */
	public String getSalesOrder() {
		return salesOrder;
	}

	/**
	 * @param salesOrder
	 *            the salesOrder to set
	 */
	public void setSalesOrder(String salesOrder) {
		this.salesOrder = salesOrder;
	}

	/**
	 * @return the requestedDeliveryDate
	 */
	public Date getRequestedDeliveryDate() {
		return requestedDeliveryDate;
	}

	/**
	 * @param requestedDeliveryDate
	 *            the requestedDeliveryDate to set
	 */
	public void setRequestedDeliveryDate(Date requestedDeliveryDate) {
		this.requestedDeliveryDate = requestedDeliveryDate;
	}

	/**
	 * @return the requestedLock
	 */
	public Boolean getRequestedLock() {
		return requestedLock;
	}

	/**
	 * @param requestedLock
	 *            the requestedLock to set
	 */
	public void setRequestedLock(Boolean requestedLock) {
		this.requestedLock = requestedLock;
	}

	/**
	 * @return the ngPackingListId
	 */
	public Integer getNgPackingListId() {
		return ngPackingListId;
	}

	/**
	 * @param ngPackingListId the ngPackingListId to set
	 */
	public void setNgPackingListId(Integer ngPackingListId) {
		this.ngPackingListId = ngPackingListId;
	}

}
