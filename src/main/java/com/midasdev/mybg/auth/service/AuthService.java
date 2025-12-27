package com.midasdev.mybg.auth.service;

import com.midasdev.mybg.auth.dto.TokenRequestUser;
import com.midasdev.mybg.auth.dto.request.AuthRequest;
import com.midasdev.mybg.config.security.Oauth.OauthAccount;
import com.midasdev.mybg.config.security.Oauth.OauthProvider;
import com.midasdev.mybg.config.security.id_token.IdToken;
import com.midasdev.mybg.config.security.id_token.IdTokenValidator;
import com.midasdev.mybg.config.security.id_token.IdTokenValidatorFactory;
import com.midasdev.mybg.config.security.jwt.AuthorizationToken;
import com.midasdev.mybg.config.security.jwt.JwtClaimResolver;
import com.midasdev.mybg.config.security.jwt.JwtProvider;
import com.midasdev.mybg.config.security.jwt.RefreshToken;
import com.midasdev.mybg.config.security.jwt.TokenAttribute;
import com.midasdev.mybg.config.security.jwt.TokenType;
import com.midasdev.mybg.config.security.jwt.repository.RefreshTokenRedisRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.assertion.Assertion;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.service.MemberService;
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
    private final MemberService memberService;

    public TokenRequestUser extractUserInfo(AuthRequest authRequest) {

        OauthProvider oauthProvider = authRequest.oauthProvider();
        String idTokenFromRequest =
                jwtClaimResolver.extractValue(
                        authRequest.authToken(),
                        TokenType.AUTH,
                        TokenAttribute.ID_TOKEN.getAttribute());
        IdTokenValidator validator = idTokenValidatorFactory.getValidator(oauthProvider);
        IdToken idToken = validator.validate(idTokenFromRequest, oauthProvider);
        return new TokenRequestUser(
                new OauthAccount(oauthProvider, idToken.sub()), idToken.nickname());
    }

    public AuthorizationToken issueAuthorizationToken(Member member) {
        AuthorizationToken authorizationToken =
                jwtProvider.createAuthorizationToken(member.getId());
        refreshTokenRedisRepository.save(RefreshToken.from(member.getId(), authorizationToken));
        return authorizationToken;
    }

    public AuthorizationToken issueAuthorizationToken(Long memberId) {
        AuthorizationToken authorizationToken = jwtProvider.createAuthorizationToken(memberId);
        refreshTokenRedisRepository.save(RefreshToken.from(memberId, authorizationToken));
        return authorizationToken;
    }

    public void verifyRefreshToken(Long memberId, String refreshToken) {
        // TEST: 저장된 refreshToken 여부에 대한 테스트
        // TEST: refreshToken 일치 여부에 대한 테스트
        RefreshToken refreshTokenFromRedis =
                refreshTokenRedisRepository
                        .findById(memberId)
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                ApplicationExceptionType.REFRESH_TOKEN_NOT_FOUND,
                                                memberId));
        Assertion.with(refreshToken)
                .setValidation(refreshTokenFromRedis::isSameToken)
                .validateOrThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionType.REFRESH_TOKEN_MISMATCH, memberId));
    }

    public void logoutMember(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        refreshTokenRedisRepository.deleteById(member.getId());
    }
}
