package com.communitychat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.communitychat.repository")
@EntityScan(basePackages = "com.communitychat.model.entity")
public class CommunityChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityChatApplication.class, args);
	}

}