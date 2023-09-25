package com.dws.challenge.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validator to check that the source and target accounts are different.
 */
@Documented
@Constraint(validatedBy = differentAccounts.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentAccountsValidator {
    String message() default "Source and target accounts must be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

