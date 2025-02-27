package com.pda.community_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.pda.community_module", "com.pda.core_module"})
public class CommunityModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityModuleApplication.class, args);
	}

}
