package com.midasdev.mybg.group.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "그룹 생성 요청")
public record GroupCreateRequest(
        @Schema(description = "그룹 이름", example = "test group", requiredMode = RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "그룹 프로필 이미지 URL", example = "https://example.com/image.jpg")
        String profileImageUrl,

        @Schema(title = "그룹 최대 인원", example = "100", requiredMode = RequiredMode.REQUIRED)
        int maxMemberCount
) {
}
