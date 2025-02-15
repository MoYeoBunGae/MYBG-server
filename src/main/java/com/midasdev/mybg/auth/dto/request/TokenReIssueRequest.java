package com.midasdev.mybg.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "access token 재발급을 위한 refresh token")
public record TokenReIssueRequest(
        @Schema(description = "refresh token(jwt)", requiredMode = Schema.RequiredMode.REQUIRED, example = "refreshToken")
        String refreshToken) {

}
