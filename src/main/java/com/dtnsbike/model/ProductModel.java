package com.dtnsbike.model;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
	//Tên sp
	@NotBlank(message = "*Vui lòng nhập tên sản phẩm.")
	private String name; 
	
	//Giá sp
	@NotNull(message = "*Vui lòng nhập giá.")
	private Integer price;

	private String sl; 
	
	//Thuế
	@NotNull(message = "*Vui lòng nhập thuế.")
	private Integer vat;
	
	//Số ngày bảo hành
	@NotNull(message = "*Vui lòng nhập số ngày bảo hành.")
	private Integer warrantyPeriod; 
	
	//Mô tả sp
	private String description;
	
	//Trạng thái sp
	private Boolean avaliable;
	
	//Loại sp
	@NotEmpty(message = "*Vui lòng chọn loại.")
	private String categoriesID;
	
	//Giảm giá
	@NotEmpty(message = "*Vui lòng chọn giảm giá.")
	private String discountsID;
	
	//Thương hiệu
	@NotEmpty(message = "*Vui lòng chọn thương hiệu.")
	private String brandsID;
	
	//Xuất sứ
	@NotEmpty(message = "*Vui lòng chọn xuất xứ.")
	private String originsID;
	
	//Chi tiết sp
//	private List<ProductDetailsModel> productDetails;
	
	//Màu sp
	private List<ProductDetailsModel> color;
	
	private MultipartFile file;
	
	private MultipartFile file2;
	
}
