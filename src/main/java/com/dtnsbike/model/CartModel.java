package com.dtnsbike.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
    
    private Integer productId;
    
    private Integer product;

    private String photo;
    
    private String name;
    
    private String size;
    
    private String message;
    
    private String color;
    
    private Double price;
    
    private Double oldPrice;
    
    private Integer qty;
    
    private Double total;

  
}
