package com.dtnsbike.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReportCustomer {
	@Id
	Integer id;
	
	String username;
	
	String fullname;
	
	String address;
	
	String phone;
	
	Double total;
	
	String count;
}
