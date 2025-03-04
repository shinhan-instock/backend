package com.pda.piggyBank_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableScheduling
@EnableFeignClients
//@EnableJpaAuditing
@ComponentScan(basePackages = {"com.pda.piggyBank_module", "com.pda.core_module"})

public class PiggyBankModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiggyBankModuleApplication.class, args);
	}

}