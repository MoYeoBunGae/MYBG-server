package com.midasdev.mybg.member.fixture;

import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.config.security.Oauth.OauthAccount;
import com.midasdev.mybg.config.security.Oauth.OauthProvider;

public class MemberFixture {
    public static Member create() {
        return Member.builder()
                .name("테스트멤버")
                .profileImageUrl("http://test.com/profile.png")
                .deleted(false)
                .oauthAccount(new OauthAccount(OauthProvider.KAKAO, "test-oauth-sub"))
                .build();
    }
}
