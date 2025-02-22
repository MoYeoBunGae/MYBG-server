package com.midasdev.mybg.group_member.controller.dto.request;

import com.midasdev.mybg.global.util.validator.IsPositiveNumber;
import jakarta.validation.constraints.NotBlank;

public record GroupJoinRequest(
        @NotBlank
        @IsPositiveNumber
        Long groupId
) {

}
