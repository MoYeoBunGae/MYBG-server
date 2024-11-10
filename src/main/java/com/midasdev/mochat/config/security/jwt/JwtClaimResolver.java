package com.midasdev.mochat.config.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtClaimResolver {

    public String extractValueWithoutValidation(String token, String key) {
        return extractFromHeader(token, key);
    }

    private String extractFromHeader(String token, String key) {
        String headerToken = token.split("\\.")[0];
        byte[] decodedHeader = Base64.getDecoder().decode(headerToken);
        Map<String, Object> headerData;
        try {
            headerData = new ObjectMapper().readValue(decodedHeader, new TypeReference<>() {});
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_PARSING_EXCEPTION, "HEADER", key);
        }

        return headerData.get(key).toString();
    }

    public String getFromClaim(Jws<Claims> claimsJws, String key) {
        return (String) claimsJws.getBody().get(key);
    }
}
