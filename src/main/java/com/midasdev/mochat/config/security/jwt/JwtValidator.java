package com.midasdev.mochat.config.security.jwt;

import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.midasdev.mochat.global.util.assertion.Assertion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtProperty jwtProperty;

    public String validate(String token, TokenType tokenType) {
        Jws<Claims> claim = validateJWT(token);
        String type = (String) claim.getBody().get(TokenAttribute.TYPE.getAttribute());
        Assertion.with(type)
                 .setValidation(tokenType::match)
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.TOKEN_TYPE_MISMATCH, type, tokenType));
        return (String) claim.getBody().get(TokenAttribute.ID_TOKEN.getAttribute());
    }

    public Jws<Claims> validateJWT(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(jwtProperty.getKey())
                       .build()
                       .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_MALFORMED);
        } catch (SignatureException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_INVALID_SIGNATURE);
        } catch (Exception e) {
            log.error("JWT parsing 중 처리되지 않은 Exception 발생", e);
            throw new ApplicationException(ApplicationExceptionType.UNDEFINED_EXCEPTION, "unknown jwt parsing exception");
        }
    }

    public Jws<Claims> validateJWT(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(key)
                       .build()
                       .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_MALFORMED);
        } catch (SignatureException e) {
            throw new ApplicationException(ApplicationExceptionType.JWT_INVALID_SIGNATURE);
        } catch (Exception e) {
            log.error("JWT parsing 중 처리되지 않은 Exception 발생", e);
            throw new ApplicationException(ApplicationExceptionType.UNDEFINED_EXCEPTION, "unknown jwt parsing exception");
        }
    }

    public String extractIdTokenFromAuthToken(String authToken) {
        return validate(authToken, TokenType.AUTH);
    }

}
