package com.midasdev.mybg.group_member.controller.dto.request;

import com.midasdev.mybg.global.util.validator.IsPositiveNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

@Schema(description = "그룹 참여 API Request Body")
public record GroupJoinRequest(
        @Schema(description = "그룹 id", example = "1", requiredMode = RequiredMode.REQUIRED)
                @NotNull
                @IsPositiveNumber
                Long groupId,
        @Schema(description = "그룹에서 사용할 닉네임 (없을 경우 사용자의 name 필드로 자동 설정)", example = "nickname")
                String nickname) {}
