package com.midasdev.mybg.auth.dto;

import com.midasdev.mybg.config.security.Oauth.OauthAccount;
import lombok.Builder;

@Builder
public record TokenRequestUser(OauthAccount oauthAccount, String nickname) {}
