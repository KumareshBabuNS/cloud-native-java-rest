package com.aljumaro.test.rest;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
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
@RequestMapping(value = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {

	private final CustomerAssembler customerAssembler;

	private final CustomerService customerService;

	@Autowired
	CustomerHypermediaRestController(CustomerAssembler cra, CustomerService customerService) {
		this.customerService = customerService;
		this.customerAssembler = cra;
	}

	@GetMapping
	ResponseEntity<Resources<Object>> root() {
		Resources<Object> objects = new Resources<>(Collections.emptyList());
		URI uri = MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection()).build()
				.toUri();
		Link link = new Link(uri.toString(), "customer");
		objects.add(link);
		return ResponseEntity.ok(objects);
	}

	@GetMapping("/customer")
	ResponseEntity<Resources<Resource<Customer>>> getCollection() {
		List<Resource<Customer>> collect = this.customerService.findAll().stream().map(customerAssembler::toResource)
				.collect(Collectors.<Resource<Customer>>toList());
		Resources<Resource<Customer>> resources = new Resources<>(collect);
		URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		resources.add(new Link(self.toString(), "self"));
		return ResponseEntity.ok(resources);
	}

	@RequestMapping(value = "/customer", method = RequestMethod.OPTIONS)
	ResponseEntity<?> options() {
		return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS,
				HttpMethod.PUT, HttpMethod.DELETE).build();
	}

	@GetMapping(value = "/customer/{id}")
	ResponseEntity<Resource<Customer>> get(@PathVariable int id) {
		return this.customerService.findById(id).map(customerAssembler::toResource).map(ResponseEntity::ok)
				.orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PostMapping(value = "/customer")
	ResponseEntity<Resource<Customer>> post(@RequestBody Customer c) {
		Customer customer = this.customerService.save(Customer.builder().name(c.getName()).email(c.getEmail()).build());
		URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/customers/{id}")
				.buildAndExpand(customer.getId()).toUri();
		return ResponseEntity.created(uri).body(this.customerAssembler.toResource(customer));
	}

	@DeleteMapping(value = "/customer/{id}")
	ResponseEntity<?> delete(@PathVariable int id) {
		return this.customerService.findById(id).map(c -> {
			customerService.delete(c);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.HEAD)
	ResponseEntity<?> head(@PathVariable int id) {
		return this.customerService.findById(id).map(exists -> ResponseEntity.noContent().build())
				.orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PutMapping("/customer/{id}")
	ResponseEntity<Resource<Customer>> put(@PathVariable Long id, @RequestBody Customer c) {
		Customer customer = this.customerService.save(Customer.builder().name(c.getName()).email(c.getEmail()).build());
		Resource<Customer> customerResource = this.customerAssembler.toResource(customer);
		URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
		return ResponseEntity.created(selfLink).body(customerResource);
	}
}
