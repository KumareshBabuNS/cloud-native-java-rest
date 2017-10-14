package com.aljumaro.test.rest;

import java.util.Optional;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.aljumaro.test.service.exception.CustomerNotFoundException;

@ControllerAdvice(annotations = RestController.class)
public class CustomerControllerAdvice {

	private MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

	@ExceptionHandler(CustomerNotFoundException.class)
	ResponseEntity<VndErrors> handleCustomerNotFoundException(Exception ex) {
		return this.error(ex, HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<VndErrors> handleIllegalArgumentException(Exception ex) {
		return this.error(ex, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
	}

	private <E extends Exception> ResponseEntity<VndErrors> error(E error, HttpStatus httpStatus, String logref) {
		String msg = Optional.ofNullable(error.getMessage()).orElse(error.getClass().getSimpleName());
		String log = Optional.ofNullable(logref).orElse("Internal Error");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(this.vndErrorMediaType);
		return new ResponseEntity<>(new VndErrors(log, msg), httpHeaders, httpStatus);
	}

}
