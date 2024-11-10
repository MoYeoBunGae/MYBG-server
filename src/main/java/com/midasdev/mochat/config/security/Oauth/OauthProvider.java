package com.midasdev.mochat.config.security.Oauth;

public enum OauthProvider {
    KAKAO("KAKAO");

    private String provider;

    OauthProvider(String provider) {
        this.provider = provider;
    }

}
