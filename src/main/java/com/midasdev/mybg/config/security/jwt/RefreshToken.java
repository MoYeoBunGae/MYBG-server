package com.midasdev.mybg.config.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refresh", timeToLive = 1209600)
public class RefreshToken {

    @Id
    private Long memberId;
    private String token;

    public static RefreshToken from(Long memberId, AuthorizationToken authorizationToken) {
        return new RefreshToken(memberId, authorizationToken.getRefreshToken());
    }

    public boolean isSameToken(String refreshToken) {
        return this.token.equals(refreshToken);
    }

}
