package com.aljumaro.test.rest;

import java.net.URI;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.aljumaro.test.repository.Customer;

@Component
public class CustomerAssembler {

	public Resource<Customer> toResource(Customer customer) {
		Resource<Customer> resource = new Resource<>(customer);

		URI photoUri = MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(CustomerProfilePhotoController.class).read(customer.getId()))
				.buildAndExpand().toUri();

		URI selfUri = MvcUriComponentsBuilder
				.fromMethodCall(
						MvcUriComponentsBuilder.on(CustomerHypermediaRestController.class).get(customer.getId()))
				.buildAndExpand().toUri();

		resource.add(new Link(selfUri.toString(), "self"));
		resource.add(new Link(photoUri.toString(), "profile-photo"));

		return resource;
	}
}
