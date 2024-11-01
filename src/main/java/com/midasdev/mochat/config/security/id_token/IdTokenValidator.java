package com.midasdev.mochat.config.security.id_token;

import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.nimbusds.jose.jwk.JWKSet;
import java.text.ParseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class IdTokenValidator {

    private final JwtValidator jwtValidator;
    private final JwtClaimResolver jwtClaimResolver;

    public IdToken validate(String idTokenFromRequest, OauthProvider oauthProvider) {
        // 1. Public key를 가져온다.
        JWKSet key = getPublicKeys(oauthProvider);

        // 2. IdToken의 kid로 적절한 public key를 가져온다 -> kakao 검증 글 참고
        // 2-1. Kid 가져오기
        String kid = jwtClaimResolver.extractValueWithoutValidation(idTokenFromRequest, "kid");

        // Public key로 검증한다.
//        Jws<Claims> claims = jwtValidator.validateJWT(idTokenFromRequest, key);
//        log.info("claims : {}", claims);


        return null;
    }

    // TODO: Cacheable에 대해 고민해보기
    private JWKSet getPublicKeys(OauthProvider oauthProvider) {
        String publicKeysJson = fetchPublicKeysJson();
        JWKSet publicKeys;
        try {
            publicKeys = JWKSet.parse(publicKeysJson);
        } catch (ParseException e) {
            throw new ApplicationException(ApplicationExceptionType.OIDC_PUBLIC_KEY_PARSING_EXCEPTION, oauthProvider);
        }

        return publicKeys;
    }

    public abstract String fetchPublicKeysJson();

}
