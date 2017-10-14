package com.aljumaro.test.rest;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aljumaro.test.proto.CustomerProtos;
import com.aljumaro.test.repository.Customer;
import com.aljumaro.test.service.CustomerService;
import com.aljumaro.test.service.exception.CustomerNotFoundException;

@RestController
@RequestMapping("/v1/protos/customer")
public class CustomerProtoController {

	private CustomerService customerService;

	public CustomerProtoController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}

	@GetMapping
	ResponseEntity<CustomerProtos.Customers> findAll() {
		Collection<Customer> all = this.customerService.findAll();

		return ResponseEntity.ok(fromCollectionToProtobuf(all));
	}

	@GetMapping(value = "/{id}")
	ResponseEntity<CustomerProtos.Customer> get(@PathVariable int id) {
		return this.customerService.findById(id).map(this::fromEntityToProtobuf).map(ResponseEntity::ok)
				.orElseThrow(() -> new CustomerNotFoundException(id));
	}

	private CustomerProtos.Customers fromCollectionToProtobuf(Collection<Customer> c) {
		return CustomerProtos.Customers.newBuilder()
				.addAllCustomer(c.stream().map(this::fromEntityToProtobuf).collect(Collectors.toList())).build();
	}

	private CustomerProtos.Customer fromEntityToProtobuf(Customer c) {
		return fromEntityToProtobuf(c.getId(), c.getName(), c.getEmail());
	}

	private CustomerProtos.Customer fromEntityToProtobuf(int id, String f, String l) {
		CustomerProtos.Customer.Builder builder = CustomerProtos.Customer.newBuilder();
		if (id > 0) {
			builder.setId(id);
		}
		return builder.setEmail(l).setName(f).build();
	}
}
