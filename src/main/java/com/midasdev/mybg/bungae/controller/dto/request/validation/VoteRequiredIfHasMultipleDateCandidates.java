package com.midasdev.mybg.bungae.controller.dto.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VoteRequiredIfHasMultipleDateCandidatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VoteRequiredIfHasMultipleDateCandidates {
    String message() default "날짜 후보가 2개 이상일 경우 투표 마감 시각(dateVoteClosedAt)은 필수입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
