package com.sipms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"com.sipms"})
@EnableJpaRepositories(basePackages = "com.sipms")
@EntityScan(basePackages = "com.sipms")

public class SipmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SipmsApplication.class, args);
	}

}
