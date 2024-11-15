package com.midasdev.mochat.config.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasdev.mochat.config.security.jwt.constant.JwtComponent;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.midasdev.mochat.global.util.assertion.Assertion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtClaimResolver {

    private final JwtValidator jwtValidator;

    public String extractValueWithoutValidation(String token, String key) {
        // REFACTOR: extractValueWithoutValidation 로 바꾸기
        return extractFromHeader(token, key);
    }

    private String extractFromHeader(String token, String key) {
        // REFACTOR: extractValueWithoutValidation 로 바꾸기
        String headerToken = token.split("\\.")[0];
        byte[] decodedHeader = Base64.getDecoder().decode(headerToken);
        Map<String, Object> headerData;
        try {
            headerData = new ObjectMapper().readValue(decodedHeader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_PARSING_EXCEPTION, "HEADER", key);
        }

        return headerData.get(key).toString();
    }

    public String extractValue(String token, TokenType tokenType, String key) {
        Jws<Claims> claimsJws = jwtValidator.validate(token, tokenType);
        return getFromClaim(claimsJws, key);
    }

    public String extractValueWithoutValidation(String token, String key, JwtComponent jwtComponent) {
        String tokenComponent = token.split("\\.")[jwtComponent.getIndex()];
        byte[] decodedComponent = Base64.getDecoder().decode(tokenComponent);
        Map<String, Object> jwtComponentData;
        try {
            jwtComponentData = new ObjectMapper().readValue(decodedComponent, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_PARSING_EXCEPTION, jwtComponent.getName());
        }
        Assertion.with(key)
                 .setValidation(jwtComponentData::containsKey)
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.JWT_CLAIMS_KEY_NOT_FOUND, key));
        return jwtComponentData.get(key).toString();
    }

    public String getFromClaim(Jws<Claims> claimsJws, String key) {
        return (String) claimsJws.getBody().get(key);
    }

}
