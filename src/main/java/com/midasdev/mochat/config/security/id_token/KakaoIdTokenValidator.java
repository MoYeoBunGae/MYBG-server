package com.midasdev.mochat.config.security.id_token;

import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.Oauth.oauth_clients.KakaoOauthClient;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.nimbusds.jose.jwk.JWKSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoIdTokenValidator extends IdTokenValidator {

    private final KakaoOauthClient kakaoOauthClient;

    public KakaoIdTokenValidator(
            JwtValidator jwtValidator,
            JwtClaimResolver jwtClaimResolver,
            KakaoOauthClient kakaoOauthClient) {
        super(jwtValidator, jwtClaimResolver);
        this.kakaoOauthClient = kakaoOauthClient;
    }

    @Override
    public JWKSet getPublicKeys() {
        String publicKeysJson = kakaoOauthClient.getKakaoOidcPublicKeys();
        JWKSet publicKeys;
        try {
            publicKeys = JWKSet.parse(publicKeysJson);
        } catch (ParseException e) {
            throw new ApplicationException(ApplicationExceptionType.OIDC_PUBLIC_KEY_PARSING_EXCEPTION, OauthProvider.KAKAO);
        }

        return publicKeys;
    }
}
