package com.project.iob.common.service;

public interface MailService {
	
	//단건
	public void sendEmail(String to, String subject, String text);

}
