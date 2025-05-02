package com.midasdev.mybg.group_member.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "그룹 멤버 프로필 수정 Request")
public record GroupMemberProfileUpdateRequest(

        @Schema(description = "그룹 내 닉네임", example = "changed_nickname", requiredMode = RequiredMode.NOT_REQUIRED)
        @Size(max = 10, message = "닉네임은 10자 이내여야 합니다.")
        String nickname,

        @Schema(description = "프로필 이미지 파일 (jpg, png, gif)", format = "binary", requiredMode = RequiredMode.NOT_REQUIRED)
        MultipartFile image

) {}
