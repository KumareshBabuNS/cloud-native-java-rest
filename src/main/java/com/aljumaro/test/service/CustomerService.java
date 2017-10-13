package com.aljumaro.test.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aljumaro.test.repository.Customer;
import com.aljumaro.test.repository.CustomerRepository;

@Service
public class CustomerService {

	private CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		super();
		this.customerRepository = customerRepository;
	}

	public Optional<Customer> findById(int id) {
		return customerRepository.findById(id);
	}

	public Collection<Customer> findAll() {
		return customerRepository.findAll();
	}

	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	public void delete(Customer customer) {
		customerRepository.delete(customer);
	}

}
