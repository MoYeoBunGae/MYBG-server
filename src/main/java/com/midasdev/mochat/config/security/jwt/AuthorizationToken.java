package com.midasdev.mochat.config.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizationToken {

    private GrantType grantType;
    private String accessToken;
    private String refreshToken;

}
