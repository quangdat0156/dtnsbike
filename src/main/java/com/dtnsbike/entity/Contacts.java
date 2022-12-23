package com.dtnsbike.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Contacts")
public class Contacts implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[contactid]")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "[username]")
	private Accounts userAr;
	
	@Column(name = "[fullname]")
    private String fullname;
	
	@Column(name = "[address]")
	private String address;
	
	@Column(name = "[phone]")
    private String phone;
	

}
