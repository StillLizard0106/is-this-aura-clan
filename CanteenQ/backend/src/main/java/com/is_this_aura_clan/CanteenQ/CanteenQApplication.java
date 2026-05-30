package com.is_this_aura_clan.CanteenQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CanteenQApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanteenQApplication.class, args);
	}

}
