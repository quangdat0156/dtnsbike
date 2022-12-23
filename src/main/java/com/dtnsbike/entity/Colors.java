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
@Table(name = "Colors")
public class Colors implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "[colorid]")
	private String id;

	@Column(name = "[name]")
	@NotBlank(message = "Tên màu sắc không được bỏ trống!")
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "colorid")
	List<ProductDetails> productdetail;

}