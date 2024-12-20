package com.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpaceshipsApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpaceshipsApiApplication.class, args);
	}
}
