package com.example.bookstorewebapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be ≥12 chars and include upper, lower, digit, and symbol";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
