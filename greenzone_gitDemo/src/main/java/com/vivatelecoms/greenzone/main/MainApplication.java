package com.vivatelecoms.greenzone.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.vivatelecoms.greenzone.main.controller.StarToCopyProcessThread;

@SpringBootApplication



//  @PropertySources({
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//application.properties"),
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//chatezee.properties"),
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//dbquery.properties")})
 
@PropertySources({
	@PropertySource("classpath:/application.properties"),
	@PropertySource("classpath:/chat.properties"),
	@PropertySource("classpath:/config_ivrchat.properties"),
	@PropertySource("classpath:/dbquery.properties")})

public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		String strClassPath = System.getProperty("java.class.path");
		System.out.println("Classpath is =" + strClassPath);
		
		
//		SmsSendThread t1 = new  SmsSendThread();
//		t1.start();
	}

}
