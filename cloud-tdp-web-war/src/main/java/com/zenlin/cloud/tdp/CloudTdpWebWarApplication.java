package com.zenlin.cloud.tdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudTdpWebWarApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudTdpWebWarApplication.class, args);
	}
}
