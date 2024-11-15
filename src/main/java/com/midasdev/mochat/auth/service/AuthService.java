package com.midasdev.mochat.auth.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.auth.dto.request.AuthRequest;
import com.midasdev.mochat.config.security.Oauth.OauthAccount;
import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.id_token.IdToken;
import com.midasdev.mochat.config.security.id_token.IdTokenValidator;
import com.midasdev.mochat.config.security.id_token.IdTokenValidatorFactory;
import com.midasdev.mochat.config.security.jwt.AuthorizationToken;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.JwtProvider;
import com.midasdev.mochat.config.security.jwt.RefreshToken;
import com.midasdev.mochat.config.security.jwt.TokenAttribute;
import com.midasdev.mochat.config.security.jwt.TokenType;
import com.midasdev.mochat.config.security.jwt.repository.RefreshTokenRedisRepository;
import com.midasdev.mochat.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtClaimResolver jwtClaimResolver;
    private final JwtProvider jwtProvider;
    private final IdTokenValidatorFactory idTokenValidatorFactory;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public TokenRequestUser extractUserInfo(AuthRequest authRequest) {

        OauthProvider oauthProvider = authRequest.oauthProvider();
        String idTokenFromRequest = jwtClaimResolver.extractValue(authRequest.authToken(), TokenType.AUTH, TokenAttribute.ID_TOKEN.getAttribute());
        IdTokenValidator validator = idTokenValidatorFactory.getValidator(oauthProvider);
        IdToken idToken = validator.validate(idTokenFromRequest, oauthProvider);
        return new TokenRequestUser(new OauthAccount(oauthProvider, idToken.sub()), idToken.nickname());
    }

    public AuthorizationToken issueAuthorizationToken(Member member) {
        AuthorizationToken authorizationToken = jwtProvider.createAuthorizationToken(member.getId());
        refreshTokenRedisRepository.save(RefreshToken.from(member.getId(), authorizationToken));
        return authorizationToken;
    }

    public AuthorizationToken issueAuthorizationToken(Long memberId) {
        AuthorizationToken authorizationToken = jwtProvider.createAuthorizationToken(memberId);
        refreshTokenRedisRepository.save(RefreshToken.from(memberId, authorizationToken));
        return authorizationToken;
    }

}
