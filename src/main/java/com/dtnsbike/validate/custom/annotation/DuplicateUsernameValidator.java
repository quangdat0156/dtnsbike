package com.dtnsbike.validate.custom.annotation;


import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.validate.custom.implement.DuplicateUsername;

public class DuplicateUsernameValidator implements ConstraintValidator<DuplicateUsername, String> {

    @Autowired
    AccountsDAO dao;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean check = true;
        if (value != null) {
            value = value.trim();
        }
        Optional<Accounts> acc = dao.findById(value);
        if (acc.isPresent()) {
            check = false;
        }
        return check;
    }
}
