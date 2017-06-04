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

/**
 * @author https://github.com/diego-torres
 *
 */
public enum IssueResolution {
	OPEN(10, "open"), 
	FIXED(20, "fixed"), 
	REOPENED(30, "reopened"), 
	UNABLE_TO_REPRODUCE(40, "unable to reproduce"), 
	NOT_FIXABLE(50, "not fixable"), 
	DUPLICATE(60, "duplicate"), 
	NO_CHANGE_REQUIRED(70, "no change required"), 
	SUSPENDED(80, "suspended"), 
	WONT_FIX_IT(90, "won't fix");

	private final int id;
	private final String resolution;

	IssueResolution(int id, String resolutionName) {
		this.id = id;
		this.resolution = resolutionName;
	}

	public int id() {
		return id;
	}

	public String resolutionName() {
		return resolution;
	}

}
