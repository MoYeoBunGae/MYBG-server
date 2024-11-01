package com.midasdev.mochat.auth.dto;

import com.midasdev.mochat.config.security.Oauth.OauthAccount;
import lombok.Builder;

@Builder
public record TokenRequestUser(OauthAccount oauthAccount, String nickname) {

}
