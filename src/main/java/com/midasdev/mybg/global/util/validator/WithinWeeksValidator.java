package com.midasdev.mybg.global.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class WithinWeeksValidator implements ConstraintValidator<WithinWeeks, LocalDate> {

    private int weeks;

    @Override
    public void initialize(WithinWeeks constraintAnnotation) {
        this.weeks = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // null 또는 빈 리스트는 검증 대상 아님
        }

        LocalDate now = LocalDate.now();
        LocalDate maxDate = now.plusWeeks(weeks);

        return !date.isAfter(maxDate);
    }
}

