package com.dtnsbike.validate.custom.implement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import com.dtnsbike.validate.custom.annotation.DuplicateInsertGmailUsersValidator;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DuplicateInsertGmailUsersValidator.class)
public @interface DuplicateInsertGmailUsers {
	String message() default "{com.dtnsbike.validate.custom.implement.DuplicateGmailUsers.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
