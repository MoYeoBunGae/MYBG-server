package com.midasdev.mochat.auth.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.auth.dto.request.AuthRequest;
import com.midasdev.mochat.config.security.Oauth.OauthAccount;
import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.id_token.IdToken;
import com.midasdev.mochat.config.security.id_token.IdTokenValidator;
import com.midasdev.mochat.config.security.id_token.IdTokenValidatorFactory;
import com.midasdev.mochat.config.security.jwt.AuthorizationToken;
import com.midasdev.mochat.config.security.jwt.JwtProvider;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
import com.midasdev.mochat.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtValidator jwtValidator;
    private final JwtProvider jwtProvider;
    private final IdTokenValidatorFactory idTokenValidatorFactory;

    public TokenRequestUser extractUserInfo(AuthRequest authRequest) {

        OauthProvider oauthProvider = authRequest.oauthProvider();
        String idTokenFromRequest = jwtValidator.extractIdTokenFromAuthToken(authRequest.authToken());
        IdTokenValidator validator = idTokenValidatorFactory.getValidator(oauthProvider);
        IdToken idToken = validator.validate(idTokenFromRequest, oauthProvider);
        return new TokenRequestUser(new OauthAccount(oauthProvider, idToken.sub()), idToken.nickname());
    }

    public AuthorizationToken issueAuthorizationToken(Member member) {
        return jwtProvider.createAuthorizationToken(member.getId());
    }

}
