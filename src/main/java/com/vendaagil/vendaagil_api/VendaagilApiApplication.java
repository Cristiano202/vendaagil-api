package com.vendaagil.vendaagil_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class VendaagilApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendaagilApiApplication.class, args);
	}
}