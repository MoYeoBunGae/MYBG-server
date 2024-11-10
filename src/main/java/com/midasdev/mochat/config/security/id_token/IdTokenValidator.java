package com.midasdev.mochat.config.security.id_token;

import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
import com.midasdev.mochat.config.security.jwt.TokenAttribute;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.security.Key;
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
        JWKSet keySet = getPublicKeys(oauthProvider);

        // 2. IdToken의 kid로 적절한 public key를 가져온다 -> kakao 검증 글 참고
        // 2-1. Kid 가져오기
        String kid = jwtClaimResolver.extractValueWithoutValidation(idTokenFromRequest, "kid");

        // 2-2. kid에 맞는 public key 가져오기
        Key publicKey;
        try {
            publicKey = keySet.getKeyByKeyId(kid).toRSAKey().toRSAPublicKey();
        } catch (JOSEException e) {
            throw new ApplicationException(ApplicationExceptionType.OIDC_PUBLIC_KEY_CONVERTING_EXCEPTION);
        }

        // 3. Public key로 검증한다.
        Jws<Claims> claims = jwtValidator.validateJWT(idTokenFromRequest, publicKey);

        // 4. IdToken의 sub 와 nickname을 가져온다. (모두 OpenId 표준)
        String sub = jwtClaimResolver.getFromClaim(claims, TokenAttribute.SUB.getAttribute());
        String nickname = jwtClaimResolver.getFromClaim(claims, TokenAttribute.NICKNAME.getAttribute());

        return new IdToken(sub, nickname);
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
