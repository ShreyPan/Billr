package com.billr.billr_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BillrBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillrBackendApplication.class, args);
	}

}
