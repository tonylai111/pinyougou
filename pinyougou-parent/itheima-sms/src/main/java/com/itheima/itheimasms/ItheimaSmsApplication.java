package com.itheima.itheimasms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.Destination;

@SpringBootApplication
public class ItheimaSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItheimaSmsApplication.class, args);
	}

	@Bean
	public Destination create(){
		return new ActiveMQQueue("pinyougou-sms");
	}

}
