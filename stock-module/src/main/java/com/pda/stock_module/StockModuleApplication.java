package com.pda.stock_module;

import com.pda.stock_module.service.FetchRankingService;
import com.pda.stock_module.service.FetchStockListService;
import com.pda.stock_module.service.FetchStockThemeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.pda.stock_module", "com.pda.core_module"})

public class StockModuleApplication {



	public static void main(String[] args) {
		SpringApplication.run(StockModuleApplication.class, args);}


}
