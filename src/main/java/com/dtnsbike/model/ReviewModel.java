package com.dtnsbike.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer star;

	private String content;
	
	private String productId;
    
	private String message;

  
}
