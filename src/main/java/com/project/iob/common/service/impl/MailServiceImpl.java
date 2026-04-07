package com.project.iob.common.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.common.dto.EmailDto;
import com.project.iob.common.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	
	@Override
	public void sendEmail(EmailDto emailDto) {
		SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.to());
        message.setSubject(emailDto.subject());
        message.setText(emailDto.text());

        mailSender.send(message);
	}

}
