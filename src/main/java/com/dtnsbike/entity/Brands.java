package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Brands")
public class Brands implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "[brandid]")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer brandid;

	@NotBlank(message = "Tên thương hiệu không được bỏ trống !")
	@Column(name = "[brand]")
	private String brand;

	@Column(name = "[image]")
	private String image;

	@Column(name = "[description]")
    private String description;
	
	@JsonIgnore
	@OneToMany(mappedBy = "brandPro")
	List<Products> products;

}