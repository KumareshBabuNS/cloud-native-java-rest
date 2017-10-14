package com.aljumaro.test.service.exception;

import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1906181991220324990L;

	//@formatter:off
	
	@Getter @Setter @Id private int id;
	
	//@formatter:on

	public CustomerNotFoundException(int id) {
		super(String.format("Customer %d could not be found", id));
		this.id = id;
	}

}
