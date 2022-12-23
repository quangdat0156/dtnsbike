package com.dtnsbike.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassForm {
	@NotBlank(message = "Vui lòng nhập mật khẩu cũ")
	private String passOld;
	
	@NotBlank(message = "Vui lòng nhập mật khẩu mới")
	@Size(min = 8, message = "Mật khẩu dài hơn 8 kí tự")
	private String passNew;
	
	@NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String passRe;
}
