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
public class ReportProducts {
	@Id
	Integer id;
	
	Integer idPro;
	
	String products;
	
	Integer amount;
	
	Double total;
	
}
