package com.midasdev.mybg.global.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsPositiveNumberValidator implements ConstraintValidator<IsPositiveNumber, Number> {

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return value != null && value.longValue() > 0;
    }
}
