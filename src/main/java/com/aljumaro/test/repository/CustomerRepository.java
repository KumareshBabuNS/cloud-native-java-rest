package com.aljumaro.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findById(int id);
}
