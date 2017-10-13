package com.aljumaro.test.rest;

import java.net.URI;
import java.util.Collection;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aljumaro.test.repository.Customer;
import com.aljumaro.test.service.CustomerService;
import com.aljumaro.test.service.exception.CustomerNotFoundException;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {

	private CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}

	@RequestMapping(method = RequestMethod.OPTIONS)
	ResponseEntity<?> options() {
		//@formatter:off
		return ResponseEntity
				.ok()
				.allow(HttpMethod.GET, HttpMethod.POST, 
						HttpMethod.PUT, HttpMethod.DELETE,
						HttpMethod.HEAD, HttpMethod.OPTIONS)
				.build();
		//@formatter:on
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	ResponseEntity<Collection<Customer>> findAll() {
		return ResponseEntity.ok(this.customerService.findAll());
	}

	@GetMapping(value = "/{id}")
	ResponseEntity<Customer> findById(@PathVariable int id) {
		return this.customerService.findById(id).map(ResponseEntity::ok)
				.orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PostMapping
	ResponseEntity<Customer> create(@RequestBody Customer customer) {
		Customer saved = this.customerService
				.save(Customer.builder().name(customer.getName()).email(customer.getEmail()).build());

		URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(saved.getId())
				.toUri();

		return ResponseEntity.created(uri).body(saved);
	}

	@DeleteMapping(value = "/{id}")
	ResponseEntity<?> delete(@PathVariable int id) {
		return this.customerService.findById(id).map(c -> {
			customerService.delete(c);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
	ResponseEntity<?> head(@PathVariable int id) {
		return this.customerService.findById(id).map(exists -> ResponseEntity.noContent().build())
				.orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PutMapping(value = "/{id}")
	ResponseEntity<Customer> put(@PathVariable int id, @RequestBody Customer c) {
		return this.customerService.findById(id).map(existing -> {

			Customer toUpdate = Customer.builder().id(existing.getId()).name(c.getName()).email(c.getEmail()).build();

			Customer customer = this.customerService.save(toUpdate);

			URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

			return ResponseEntity.created(selfLink).body(customer);
		}).orElseThrow(() -> new CustomerNotFoundException(id));

	}
}
