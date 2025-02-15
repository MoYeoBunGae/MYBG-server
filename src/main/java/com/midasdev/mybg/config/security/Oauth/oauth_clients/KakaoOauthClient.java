package com.midasdev.mybg.config.security.Oauth.oauth_clients;

import com.midasdev.mybg.global.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "KakaoOauthClient", url = "https://kauth.kakao.com")
public interface KakaoOauthClient {

    @Cacheable(value = Cache.OIDC_PUBLIC_KEYS, key = "'kakao'")
    @GetMapping("/.well-known/jwks.json")
    String getKakaoOidcPublicKeys();
}
