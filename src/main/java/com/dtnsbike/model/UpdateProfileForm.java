package com.dtnsbike.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileForm {
    private String username;
    
    @NotBlank
    private String firstname;


    private String middlename;

    @NotBlank
    private String lastname;

    @NotBlank
    private String email;

    @NotBlank
    private String photo;

    @NotBlank
    private String phone;

    @NotBlank
    private String gender;

    private String fullname;
    
    @NotBlank
    private String day;

    @NotBlank
    private String month;

    @NotBlank
    private String year;
    
    private String message;

}
