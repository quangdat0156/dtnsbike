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
@Table(name = "Authorities")
public class Authorities implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name="[authoritiesid]")
	private Integer authoritiesid;

	@ManyToOne
	@JoinColumn(name = "[username]")
	private Accounts username;

	@ManyToOne
	@JoinColumn(name = "[roleid]")
	private Roles roleId;
	
	@Column(name = "[active]")
    private Boolean active = true;
}
