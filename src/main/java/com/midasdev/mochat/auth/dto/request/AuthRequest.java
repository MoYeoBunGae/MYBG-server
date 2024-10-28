package com.midasdev.mochat.auth.dto.request;

import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull OauthProvider oauthProvider, @NotBlank String authToken) {
    //TODO: OauthProvider가 잘못들어오는 경우를 생각해 String으로 받고 Custom Validation을 적용시키는 것이 어떨까? 아니면 가능하면 그냥 여기 바로 Validation 적용
}
