package com.midasdev.mochat.config.security.id_token;

import com.midasdev.mochat.config.security.Oauth.oauth_clients.KakaoOauthClient;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.JwtValidator;
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
    public String fetchPublicKeysJson() {
        return kakaoOauthClient.getKakaoOidcPublicKeys();
    }
}
