package com.midasdev.mybg.auth.dto.request;

import com.midasdev.mybg.config.security.Oauth.OauthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Social 인증 후 발급받은 authToken")
public record AuthRequest(
        @Schema(description = "Social 인증 제공자", example = "KAKAO", requiredMode = RequiredMode.REQUIRED)
        @NotNull OauthProvider oauthProvider,

        @Schema(description = "Social 인증 후 발급받은 auth token(jwt)", example = "authToken", requiredMode = RequiredMode.REQUIRED)
        @NotBlank String authToken) {
    //TODO: OauthProvider가 잘못들어오는 경우를 생각해 String으로 받고 Custom Validation을 적용시키는 것이 어떨까? 아니면 가능하면 그냥 여기 바로 Validation 적용
}
