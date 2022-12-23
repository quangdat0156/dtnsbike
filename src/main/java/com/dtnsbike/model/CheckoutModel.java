package com.dtnsbike.model;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String addressId;
    
	private List<CartModel> cart;
	
	private String saleId;
	
	private Double shipping;
	
	private String statusId;
}
