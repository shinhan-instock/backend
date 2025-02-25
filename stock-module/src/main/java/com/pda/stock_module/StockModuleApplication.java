package com.pda.stock_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockModuleApplication.class, args);
	}

}
