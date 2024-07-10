package com.example.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.example.notes.repository")
@ComponentScan(basePackages = "com.example.notes.repository")
@ComponentScan(basePackages = "com.example.notes.*")
@ComponentScan(basePackages = "com.example.notes.config.*")

public class SendNotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SendNotesApplication.class, args);
	}

}
