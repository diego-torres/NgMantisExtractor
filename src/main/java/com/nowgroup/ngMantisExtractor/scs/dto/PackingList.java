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
package com.nowgroup.ngMantisExtractor.scs.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author https://github.com/diego-torres
 *
 */
@Entity
@Table(name="scs_packing_list", schema="scs_io")
public class PackingList implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer packingListId;
	private String salesOrder;
	private String packingList;
	private Date deliveryDate;
	private Boolean hold = false;

	/**
	 * @return the packingListId
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getPackingListId() {
		return packingListId;
	}

	/**
	 * @param packingListId
	 *            the packingListId to set
	 */
	public void setPackingListId(Integer packingListId) {
		this.packingListId = packingListId;
	}

	/**
	 * @return the salesOrder
	 */
	@Column
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
	 * @return the packingList
	 */
	@Column
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
	 * @return the deliveryDate
	 */
	@Column
	@Temporal(TemporalType.DATE)
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate
	 *            the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the hold
	 */
	@Column
	public Boolean getHold() {
		return hold;
	}

	/**
	 * @param hold
	 *            the hold to set
	 */
	public void setHold(Boolean hold) {
		this.hold = hold;
	}

}
