package com.midasdev.mochat.auth.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.auth.dto.request.AuthRequest;
import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.id_token.IdToken;
import com.midasdev.mochat.config.security.id_token.IdTokenValidator;
import com.midasdev.mochat.config.security.id_token.IdTokenValidatorFactory;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtValidator jwtValidator;
    private final IdTokenValidatorFactory idTokenValidatorFactory;

    public TokenRequestUser extractUserInfo(AuthRequest authRequest) {

        OauthProvider oauthProvider = authRequest.oauthProvider();
        String idTokenFromRequest = jwtValidator.extractIdTokenFromAuthToken(authRequest.authToken());
        IdTokenValidator validator = idTokenValidatorFactory.getValidator(oauthProvider);
        IdToken idToken = validator.validate(idTokenFromRequest, oauthProvider);
        return new TokenRequestUser(authRequest.oauthProvider(), idToken.getSub(), idToken.getName());
    }

}
