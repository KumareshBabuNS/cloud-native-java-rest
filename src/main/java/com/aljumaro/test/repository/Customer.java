package com.aljumaro.test.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// JPA
@Entity

// LOMBOK
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	//@formatter:off
	
	@Getter @Setter @Id @GeneratedValue private int id;
	@Getter @Setter private String name;
	@Getter @Setter private String email;
	
	//@formatter:on

}
