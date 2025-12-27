package com.midasdev.mybg.config.security.Oauth;

public enum OauthProvider {
    KAKAO("KAKAO");

    private String provider;

    OauthProvider(String provider) {
        this.provider = provider;
    }
}
