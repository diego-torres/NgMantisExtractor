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

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.nowgroup.ngMantisExtractor.mbt.repo.UserRepository;

import biz.futureware.mantisconnect.IssueData;

/**
 * @author https://github.com/diego-torres
 *
 */
@Controller
public class MantisSoapClient {

	@Value("${NgMantisExtractor.web.username}")
	private String mtbtUserName;

	@Value("${NgMantisExtractor.web.password}")
	private String mtbtPassword;

	@Value("${NgMantisExtractor.web.endpoint}")
	private String mtbtApiEndpoint = "http://localhost/mantisbt/api/soap/mantisconnect.php";

	public String ackNAssign(IssueData issueData) {
		String result = "OK";
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			SOAPMessage soapResponse = soapConnection.call(ackNAssignSoapRequest(issueData), mtbtApiEndpoint);
			String sResponse = soapResponse.getSOAPBody().getTextContent();
			if (sResponse.equals("true"))
				result = "OK";
			else
				result = "FAIL";
			System.out.println(sResponse);
		} catch (UnsupportedOperationException | SOAPException e) {
			e.printStackTrace();
			result = "FAIL";
		}

		return result;
	}

	private SOAPMessage ackNAssignSoapRequest(IssueData issueData) throws SOAPException {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("man", "http://futureware.biz/mantisconnect");

		SOAPBody soapBody = envelope.getBody();
		SOAPElement issueUpdateElement = soapBody.addChildElement("mc_issue_update", "man");
		issueUpdateElement.addChildElement("username").addTextNode(mtbtUserName);
		issueUpdateElement.addChildElement("password").addTextNode(mtbtPassword);
		issueUpdateElement.addChildElement("issueId").addTextNode(String.valueOf(issueData.getId()));

		SOAPElement issueElement = issueUpdateElement.addChildElement("issue");
		issueElement.addChildElement("id").addTextNode(String.valueOf(issueData.getId()));
		issueElement.addChildElement("category").addTextNode(issueData.getCategory());
		issueElement.addChildElement("summary").addTextNode(issueData.getSummary());
		issueElement.addChildElement("description").addTextNode(issueData.getDescription());

		SOAPElement issueStatusElement = issueElement.addChildElement("status");
		issueStatusElement.addChildElement("id").addTextNode(String.valueOf(issueData.getiStatus().id()));
		issueStatusElement.addChildElement("name").addTextNode(String.valueOf(issueData.getiStatus().statusName()));

		SOAPElement handlerElement = issueElement.addChildElement("handler");
		handlerElement.addChildElement("id").addTextNode(String.valueOf(issueData.getHandler().getId()));
		handlerElement.addChildElement("name").addTextNode(String.valueOf(issueData.getHandler().getUserName()));
		handlerElement.addChildElement("real_name").addTextNode(String.valueOf(issueData.getHandler().getRealName()));
		handlerElement.addChildElement("email").addTextNode(String.valueOf(issueData.getHandler().getEmail()));

		SOAPElement reporterElement = issueElement.addChildElement("reporter");
		reporterElement.addChildElement("id").addTextNode(String.valueOf(issueData.getReporter().getId()));
		reporterElement.addChildElement("name").addTextNode(String.valueOf(issueData.getReporter().getUserName()));
		reporterElement.addChildElement("real_name").addTextNode(String.valueOf(issueData.getReporter().getRealName()));
		reporterElement.addChildElement("email").addTextNode(String.valueOf(issueData.getReporter().getEmail()));
		
		SOAPElement projectElement = issueElement.addChildElement("project");
		projectElement.addChildElement("id").addTextNode(String.valueOf(issueData.getProject().getId()));
		projectElement.addChildElement("name").addTextNode(issueData.getProject().getName());

		soapMessage.saveChanges();
		return soapMessage;
	}

}
