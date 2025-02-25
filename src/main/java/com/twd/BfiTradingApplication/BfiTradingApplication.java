package com.twd.BfiTradingApplication;

import com.twd.BfiTradingApplication.service.CSVUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BfiTradingApplication {

	@Autowired
	private CSVUploadService csvUploadService;

	public static void main(String[] args) {

		SpringApplication.run(BfiTradingApplication.class, args);
	}


	@Bean
	public CommandLineRunner loadCSVDataOnStartup() {
		return args -> {
			// First, upload all Currency details (so that CrossParity lookup works)
			csvUploadService.uploadCurrencyDetails();
			// Then, upload all CrossParity details
			csvUploadService.uploadCrossParityDetails();
		};
	}

}
