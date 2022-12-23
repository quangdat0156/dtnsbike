package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Status")
public class Status implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[statusid]")
	private String id;

	@Column(name = "[name]")
	private String name;
	
	@Column(name = "[description]")
    private String description;

	@JsonIgnore
	@OneToMany(mappedBy = "statusId")
	List<Orders> statusOrders;

}