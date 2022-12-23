package com.dtnsbike.model;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

import com.dtnsbike.validate.custom.implement.DuplicateInsertGmailUsers;
import com.dtnsbike.validate.custom.implement.DuplicatePhone;
import com.dtnsbike.validate.custom.implement.DuplicateUsername;


public class AccountAdminModel implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank
    @DuplicateUsername
    private String username;

    @NotBlank
    private String firstname;

    @NotBlank
    private String middlename;

    @NotBlank
    private String lastname;

    @NotBlank
    @Email
    @DuplicateInsertGmailUsers
    private String email;

    @NotBlank
    @Pattern(regexp = "(.{0})|^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$")
    @DuplicatePhone
    private String phone;

    @NotBlank
    private String gender;

    @NotBlank
    private String birthday;

    private MultipartFile file;

    @AssertFalse
    @AssertTrue
    private Boolean active;

    @NotEmpty
    private List<String> role;

    public String getUsername() {
        if (username != null) {
            return username.trim();
        }
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
    }

    public String getFirstname() {
        if (firstname != null) {
            return firstname.trim();
        }
        return firstname;
    }

    public void setFirstname(String firstname) {
        if (firstname != null) {
            firstname = firstname.trim();
        }
        this.firstname = firstname;
    }

    public String getMiddlename() {
        if (middlename != null) {
            return middlename.trim();
        }
        return middlename;
    }

    public void setMiddlename(String middlename) {
        if (middlename != null) {
            middlename = middlename.trim();
        }
        this.middlename = middlename;
    }

    public String getLastname() {
        if (lastname != null) {
            return lastname.trim();
        }
        return lastname;
    }

    public void setLastname(String lastname) {
        if (lastname != null) {
            lastname = lastname.trim();
        }
        this.lastname = lastname;
    }

    public String getEmail() {
        if (email != null) {
            return email.trim();
        }
        return email;
    }

    public void setEmail(String email) {
        if (email != null) {
            email = email.trim();
        }
        this.email = email;
    }

    public String getPhone() {
        if (phone != null) {
            return phone.trim();
        }
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null) {
            phone = phone.trim();
        }
        this.phone = phone;
    }

    public String getGender() {
        if (gender != null) {
            return gender.trim();
        }
        return gender;
    }

    public void setGender(String gender) {
        if (gender != null) {
            gender = gender.trim();
        }
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

}
