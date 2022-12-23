package com.dtnsbike.validate.custom.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.validate.custom.implement.DuplicatePhone;

public class DuplicatePhoneValidator implements ConstraintValidator<DuplicatePhone, String> {

    @Autowired
    AccountsDAO dao;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean check = true;
        if (value != null) {
            value = value.trim();
        }
        Accounts acc = dao.findByPhones(value);
        if (acc!=null) {
            check = false;
        }
        return check;
    }
}
