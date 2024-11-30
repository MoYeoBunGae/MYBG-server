package com.midasdev.mochat.global.exception;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionType {
    // member
    MEMBER_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "ERR_MEMBER_001", "해당 ID의 회원을 찾을 수 없습니다. : {0}"),

    // authentication
    TOKEN_AUTHENTICATION_EXCEPTION(HttpStatus.FORBIDDEN, "ERR_AUTH_001", "토큰 인증에 실패했습니다. : {0}"),
    NOT_BEARER_TOKEN(HttpStatus.FORBIDDEN, "ERR_AUTH_002", "Bearer Token이 아닙니다."),
    AUTHORIZATION_HEADER_NOT_FOUND(HttpStatus.FORBIDDEN, "ERR_AUTH_003", "Authorization Header가 없거나 비어있습니다."),

    // jwt
    JWT_EXPIRED(HttpStatus.BAD_REQUEST, "ERR_JWT_001", "JWT 기한이 만료되었습니다."),
    JWT_MALFORMED(HttpStatus.BAD_REQUEST, "ERR_JWT_002", "JWT가 손상되었습니다."),
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "ERR_JWT_003", "지원되지 않는 JWT 입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "ERR_JWT_004", "signature가 유효하지 않습니다."),
    /**
     * - {0} : JWT Component name
     */
    JWT_PARSING_EXCEPTION(HttpStatus.BAD_REQUEST, "ERR_JWT_005", "JWT {0} 파싱 중 오류가 발생했습니다."),
    /**
     * - {0} : JWT Claims Key
     */
    JWT_CLAIMS_KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "ERR_JWT_006", "JWT Claims Key를 찾을 수 없습니다. : {0}"),

    // token
    /**
     * 토큰 타입이 맞지 않을 때 <br>
     * - {0} : 전달된 토큰 타입<br>
     * - {1} : 필요한 토큰 타입
     */
    TOKEN_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ERR_TOKEN_001", "토큰 타입이 맞지 않습니다. (전달된 토큰 : {0}, 필요한 토큰 {1})"),
    /**
     * - {0} : memberId
     */
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "ERR_TOKEN_002", "MemberId {0}의 Refresh Token을 찾을 수 없습니다."),
    /**
     * - {0} : memberId
     */
    REFRESH_TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "ERR_TOKEN_003", "MemberId {0}의 Refresh Token이 일치하지 않습니다."),

    // oidc public key
    OIDC_PUBLIC_KEY_PARSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_OIDC_001", "Provider {0}의 인증 public keys 에서 에러가 발생했습니다."),
    OIDC_PUBLIC_KEY_CONVERTING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_OIDC_002", "Public Key 연산 중 에러가 발생했습니다. - Converting"),

    // global
    FILTER_OR_API_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_001", "Filter 에러 또는 API 로직 중 처리되지 못한 에러 발생 : {0}"),
    /**
     * - {0} : Exception Message
     */
    GLOBAL_BAD_REQEUST(HttpStatus.BAD_REQUEST, "ERR_GLOBAL_002", "잘못된 요청입니다. : {0}"),
    /**
     * - {0} : Exception Message
     */
    GLOBAL_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_003", "서버 내부 에러입니다. : {0}"),
    UNDEFINED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_GLOBAL_999", "정의되지 않은 에러입니다. : {0}");

    public static ApplicationExceptionType resolveExceptionType(Exception exception) {
        if (exception instanceof BindException) {
            return ApplicationExceptionType.GLOBAL_BAD_REQEUST;
        }
        return ApplicationExceptionType.UNDEFINED_EXCEPTION;
    }

    private final HttpStatus httpStatus;
    private final String exceptionCode;
    private final String errorMessage;

    public String getErrorMessage(Object... args) {
        return MessageFormat.format(errorMessage, args);
    }
}
