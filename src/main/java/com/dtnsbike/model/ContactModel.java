package com.dtnsbike.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fullname;
    
    private String phone;
    
    private String adr;

  
}
