package com.example.finance_dashboard_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class FinanceDashboardBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDashboardBackendApplication.class, args);
	}

}
