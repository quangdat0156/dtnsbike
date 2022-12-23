package com.dtnsbike.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.dtnsbike.validate.custom.implement.DuplicateUsername;

public class UserResetpassModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank
    @DuplicateUsername
    private String username;

	public String getUsername() {
		if (this.username != null) {
			return username.trim();
		}
		return username;
	}

	public void setUsername(String username) {
		if (username != null) {
			username.trim();
		}
		this.username = username;
	}

}
