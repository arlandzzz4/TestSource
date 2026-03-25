package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { 
	    org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class 
	})
public class IoBApplication {

	public static void main(String[] args) {
		SpringApplication.run(IoBApplication.class, args);
	}

}
