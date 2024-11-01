package com.midasdev.mochat.global.exception;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionType {

    // jwt
    JWT_EXPIRED(HttpStatus.BAD_REQUEST, "ERR_JWT_001", "JWT 기한이 만료되었습니다."),
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "ERR_JWT_002", "JWT가 손상되었습니다."),
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "ERR_JWT_003", "지원되지 않는 JWT 입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "ERR_JWT_004", "signature가 유효하지 않습니다."),

    // token
    /**
     * 토큰 타입이 맞지 않을 때 <br>
     * - {0} : 전달된 토큰 타입<br>
     * - {1} : 필요한 토큰 타입
     */
    TOKEN_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ERR_TOKEN_001", "토큰 타입이 맞지 않습니다. (전달된 토큰 : {0}, 필요한 토큰 {1})"),

    // jwk
    OIDC_PUBLIC_KEY_PARSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_JWK_001", "Provider {0}의 인증 public keys 에서 에러가 발생했습니다."),

    // global
    FILTER_OR_API_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_001", "Filter 에러 또는 API 로직 중 처리되지 못한 에러 발생 : {0}"),
    UNDEFINED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_999", "정의되지 않은 에러입니다. : {0}");

    private final HttpStatus httpStatus;
    private final String exceptionCode;
    private final String errorMessage;

    public String getErrorMessage(Object... args) {
        return MessageFormat.format(errorMessage, args);
    }
}
