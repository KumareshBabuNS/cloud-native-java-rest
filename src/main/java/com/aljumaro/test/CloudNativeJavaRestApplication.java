package com.aljumaro.test;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.aljumaro.test.repository.Customer;
import com.aljumaro.test.service.CustomerService;

@SpringBootApplication
public class CloudNativeJavaRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudNativeJavaRestApplication.class, args);
	}

	@Bean
	CommandLineRunner init(CustomerService customerService) {
		return args -> Arrays
				.stream(("Mark,Fisher;Scott,Frederick;Brian,Dussault;Josh,Long;Kenny,Bastani;Dave,Syer;Spencer,Gibb")
						.split(";"))
				.map(n -> n.split(","))
				.map(tpl -> customerService.save(Customer.builder().name(tpl[0]).email(tpl[1]).build()))
				.forEach(System.out::println);
	}
}
