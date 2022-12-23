package com.dtnsbike.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orderdetails")
public class OrderDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[orderdetailid]")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "[orderid]")
    private Orders ordersId;

	@ManyToOne
	@JoinColumn(name = "[productdetailid]")
	private ProductDetails productsId;

	@Column(name = "[price]")
	private Double price;
	
	@Column(name = "[size]")
    private String size;

	@Column(name = "[amount]")
	private Integer amount;

	@Column(name = "[vat]")
    private Double vat;
	
	@Column(name = "[discount]")
    private Double discount;
	
	@Column(name = "[warrantyperiod]")
    private Integer warrantyPeriod;
	

}