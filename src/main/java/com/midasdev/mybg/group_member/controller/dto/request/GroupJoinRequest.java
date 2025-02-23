package com.midasdev.mybg.group_member.controller.dto.request;

import com.midasdev.mybg.global.util.validator.IsPositiveNumber;
import jakarta.validation.constraints.NotNull;

public record GroupJoinRequest(
        @NotNull
        @IsPositiveNumber
        Long groupId,

        String nickname
) {

}
