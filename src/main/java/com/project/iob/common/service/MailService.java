package com.project.iob.common.service;

import com.project.iob.common.dto.EmailDto;

public interface MailService {
	
	//단건
	public void sendEmail(EmailDto emailDto);

}
