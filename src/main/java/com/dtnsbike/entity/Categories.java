package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Categories")
public class Categories implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[categoryid]")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "[typeid]")
	@NotNull(message = "Vui lòng chọn danh mục !")
	private Types typeId;
	
	@Column(name = "[name]")
	@NotBlank(message = "Tên danh mục không được bỏ trống!")
    private String name;

	@Column(name = "[description]")
    private String description;
	
	@JsonIgnore
	@OneToMany(mappedBy = "catePro")
	List<Products> products;

}