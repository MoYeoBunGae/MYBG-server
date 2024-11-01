package com.midasdev.mochat.auth.dto;

import com.midasdev.mochat.config.security.Oauth.OauthAccount;
import com.midasdev.mochat.config.security.Oauth.OauthProvider;
import lombok.Builder;

@Builder
public record TokenRequestUser(OauthProvider provider, String sub, String name) {
    public OauthAccount toOauthAccount() {
        return new OauthAccount(provider, sub);
    }

}
