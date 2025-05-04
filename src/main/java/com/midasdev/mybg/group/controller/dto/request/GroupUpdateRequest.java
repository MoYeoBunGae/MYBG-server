package com.midasdev.mybg.group.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "그룹 정보 수정 Request")
public record GroupUpdateRequest(

        @Schema(
                description = "변경할 그룹 이름 (최대 25자)",
                example = "우리 가족방",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        @Size(max = 25, message = "그룹 이름은 최대 25자까지 가능합니다.")
        String name,

        @Schema(
                description = "변경할 그룹 프로필 이미지 (jpg, jpeg, png, gif), 변경하지 않을 시 null, 최대 3MB",
                format = "binary",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        MultipartFile profileImage,

        @Schema(
                description = "최대 인원 수 (최대 1000명)",
                example = "10",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        @Max(value = 1000, message = "최대 인원 수는 1000명을 초과할 수 없습니다.")
        Integer maxMemberCount

) {}
