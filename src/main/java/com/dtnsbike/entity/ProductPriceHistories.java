package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Productpricehistories")
public class ProductPriceHistories implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[productpricehistoryid]")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "[username]")
	private Accounts userPro_by_His;
	
	@ManyToOne
    @JoinColumn(name = "[productid]")
    private Products productid;

	@Column(name = "[priceold]")
	private Double priceold;
	
	@Column(name = "[pricenew]")
    private Double pricenew;


	@Column(name = "[timechange]")
	@Temporal(TemporalType.DATE)
	private Date timeChange = new Date();
}
