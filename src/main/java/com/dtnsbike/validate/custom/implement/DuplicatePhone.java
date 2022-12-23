package com.dtnsbike.validate.custom.implement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import com.dtnsbike.validate.custom.annotation.DuplicatePhoneValidator;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DuplicatePhoneValidator.class)
public @interface DuplicatePhone {
    String message() default "{com.dtnsbike.validate.custom.implement.DuplicatePhone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
