package com.midasdev.mybg.config.security.jwt;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("jwt")
public class JwtProperty {

    private String secretKey;
    private Integer authTokenExpiredSecond;
    private Integer accessTokenExpiredSecond;
    private Integer refreshTokenExpiredSecond;

    @Bean
    public Key getKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
}
