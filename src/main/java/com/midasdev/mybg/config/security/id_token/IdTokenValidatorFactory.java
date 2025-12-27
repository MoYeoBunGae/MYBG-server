package com.midasdev.mybg.config.security.id_token;

import com.midasdev.mybg.config.security.Oauth.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdTokenValidatorFactory {

    private final KakaoIdTokenValidator kakaoIdTokenValidator;

    public IdTokenValidator getValidator(OauthProvider oauthProvider) {
        return switch (oauthProvider) {
            case KAKAO -> kakaoIdTokenValidator;
        };
    }
}
