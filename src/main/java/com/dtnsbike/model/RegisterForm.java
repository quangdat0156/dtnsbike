package com.dtnsbike.model;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {
@NotBlank(message = "Vui lòng nhập tên tài khoản")
private String username;

@NotBlank(message = "Vui lòng nhập mật khẩu")
private String password;

//@NotBlank(message = "Vui lòng nhập họ và tên")
private String fullname;

@NotBlank(message = "Vui lòng nhập email")
private String email;

@NotBlank(message = "Vui lòng nhập Số điện thoại")
private String phone;

@NotBlank(message = "Vui lòng nhập xác nhận mật khẩu")
private String repassword;
}
