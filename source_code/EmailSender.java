package com.ezesoft.aqa.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

import com.ezesoft.aqa.common.ConfigurationDetails;
import com.ezesoft.aqa.common.DateUtil;
import com.ezesoft.aqa.common.ObjectUtil;

public class EmailSender {

	private static ConfigurationDetails configDetails;
    private static EmailSender emailSender;

    private EmailSender() {
        init();
    }

    /**
     * This method will initialize the Configuration details.
     */
    private void init() {
    	ObjectUtil util = new ObjectUtil();
        configDetails = util.getConfigurationDetailsObject();

    }

    /**
     * This method returns the EmailSender object with ready to use state.
     * @return EmailSender object
     */
    public static EmailSender geInstance() {
        if(emailSender == null) {
            emailSender = new EmailSender();
        }
        return emailSender;
    }

    private void sendMail(String dateStr) {
        String htmlBodyContent = ReportGenerator.generateConsolidatedReport(dateStr);
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", configDetails.getEmailHostname());
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(configDetails.getSender()));
            //TO DO :: Update with list of recipients address
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(configDetails.getRecipients()[0]));
            message.setSubject("PMA Automation Script Execution report : " + dateStr);
            message.setContent(htmlBodyContent, "text/html");
            Transport.send(message);
            System.out.println("Mail successfully sent");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void sendReport(String dateStr) {
    	sendMail(dateStr);
    }
    
    public void sendReport() {
    	sendMail(DateUtil.convertDateToString(new Date(), DateUtil.DEFAUL_FORMAT));
    }
    public void sendReport(String dateStr, String fromAddress, String toAddress) {
    	if(dateStr == null || StringUtils.isEmpty(dateStr)) {
    		dateStr = DateUtil.convertDateToString(new Date(), DateUtil.DEFAUL_FORMAT);
    	}
    	if(fromAddress == null || fromAddress == "") {
    		fromAddress = configDetails.getSender();
    	}
    	if(toAddress == null || toAddress == "") {
    		toAddress = configDetails.getRecipients()[0];
    	}
    	String htmlBodyContent = ReportGenerator.generateConsolidatedReport(dateStr);
    	System.out.println("****** Email details *******");
    	System.out.println("date str = " + dateStr);
    	sendMail(dateStr, fromAddress, toAddress, htmlBodyContent);
    }
    
    private void sendMail(String dateStr, String fromAddress, String toAddress, String htmlBodyContent) {
        
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", configDetails.getEmailHostname());
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.addRecipients(Message.RecipientType.TO, getToAddressesFromString(toAddress));
            //message.setSubject("PMA UI Automation Execution report : " + dateStr);
            message.setSubject("PMA UI Automation Execution Regression Report of \"" + dateStr + "\"");
            message.setContent(htmlBodyContent, "text/html");
            Transport.send(message);
            System.out.println("Mail successfully sent");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private  Address[] getToAddressesFromString(String toAddresses) throws AddressException {
    	List<InternetAddress> toAddressList = new ArrayList<InternetAddress>();
    	Address[] toAddressArr;
    	String[] splitAddressArr = toAddresses.split(",");
    	for(int i=0; i<splitAddressArr.length; i++) {
    		toAddressList.add(new InternetAddress(splitAddressArr[i].trim()));
    	}
    	toAddressArr = new Address[toAddressList.size()];
    	toAddressArr = toAddressList.toArray(toAddressArr);
    	return toAddressArr;
    	 
    }
    
}
