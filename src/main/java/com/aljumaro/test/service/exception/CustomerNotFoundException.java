package com.aljumaro.test.service.exception;

import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1906181991220324990L;

	//@formatter:off
	
	@Getter @Setter @Id private int id;
	
	//@formatter:on

}
