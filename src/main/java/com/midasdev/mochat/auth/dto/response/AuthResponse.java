package com.midasdev.mochat.auth.dto.response;

import com.midasdev.mochat.config.security.jwt.AuthorizationToken;
import com.midasdev.mochat.member.domain.Member;
import lombok.Builder;

@Builder
public record AuthResponse(
        boolean isNewMember,
        String accessToken,
        String refreshToken,
        Long memberId,
        String memberName
) {
    public static AuthResponse from(Member member, AuthorizationToken authorizationToken, boolean isNewMember) {
        return AuthResponse.builder()
                .isNewMember(isNewMember)
                .accessToken(authorizationToken.getAccessToken())
                .refreshToken(authorizationToken.getRefreshToken())
                .memberId(member.getId())
                .memberName(member.getName())
                .build();
    }
}
