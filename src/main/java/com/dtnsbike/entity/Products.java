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
@Table(name = "Products")
public class Products implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[productid]")
	private Integer id;
	
	@Column(name = "[name]")
	private String name;
	
	@Column(name = "[price]")
	private Double price;
	
	@Column(name="[vat]")
	private Double vat;
	
	@Column(name = "[warrantyperiod]")
    private Integer warrantyperiod;	
	
	@Column(name = "[avaliable]")
	private Boolean avaliable;
	
	@ManyToOne
	@JoinColumn(name = "[categoryid]")
	private Categories catePro;
	
	@Column(name = "[description]")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "[brandid]")
	private Brands brandPro;
	
	@ManyToOne
    @JoinColumn(name = "[discountid]")
    private Discounts discountid;

	@ManyToOne
	@JoinColumn(name = "[originid]")
	private Origins originid;
	
	@Column(name = "[image]")
	private String img;
	
	@JsonIgnore
	@OneToMany(mappedBy = "productid")
	List<ProductPriceHistories> priceHistories;
	
	@JsonIgnore
	@OneToMany(mappedBy = "productid")
	List<ProductDetails> productDetail; 
	
	@JsonIgnore
    @OneToMany(mappedBy = "productid")
    List<ProductReviews> productReview; 
	
	@JsonIgnore
	@OneToMany(mappedBy = "proCart")
	List<Carts> carts;
	
	@JsonIgnore
	@OneToMany(mappedBy = "productsId")
	List<Favorites> favorites;
	
	@JsonIgnore
	@OneToMany(mappedBy = "productid")
	List<DetailPhotos> detailphoto;
}