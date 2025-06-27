package com.midasdev.mybg.bungae.controller.dto.request.validation;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VoteRequiredIfHasMultipleDateCandidatesValidator implements ConstraintValidator<VoteRequiredIfHasMultipleDateCandidates, BungaeCreateRequest> {

    @Override
    public boolean isValid(BungaeCreateRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.dateCandidates() == null) return true;
        if (value.dateCandidates().size() <= 1) return true;
        // 날짜 후보가 2개 이상일 때 dateVoteClosedAt이 null이면 검증 실패
        if (value.dateVoteClosedAt() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("dateVoteClosedAt")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
