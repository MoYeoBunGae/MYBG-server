package com.midasdev.mybg.global.util.validator;

import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 0보다 큰 양의 정수인지 체크합니다.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsPositiveNumberValidator.class)
public @interface IsPositiveNumber {
    String message() default "숫자는 0보다 커야 합니다.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
