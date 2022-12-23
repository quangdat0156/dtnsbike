package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Orders implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[orderid]")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="[username]")
	private Accounts userOrder;
	
	@Column(name = "[fullname]")
	private String fullname;
	
	@Column(name = "[phone]")
	private String phone;
	
	@Column(name = "[address]")
	private String address;
	
	@ManyToOne
	@JoinColumn(name="[saleid]")
	private Sales saleId;
	
	@Column(name = "[salevalue]")
    private Integer salevalue;	
	
	@Column(name = "[purchasedate]")
	@Temporal(TemporalType.DATE)
	private Date date = new Date();
	
	@ManyToOne
	@JoinColumn(name="[statusid]")
	private Status statusId;
	
	@JsonIgnore
	@OneToMany(mappedBy = "ordersId")
	List<OrderDetails> orderdetail;
	
}
