package com.midasdev.mybg.global.exception;

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

    // group
    GROUP_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "ERR_GROUP_001", "해당 ID의 그룹을 찾을 수 없습니다. : {0}"),
    GROUP_NOT_FOUND_BY_INVITATION_CODE(HttpStatus.BAD_REQUEST, "ERR_GROUP_002", "해당 초대 코드의 그룹을 찾을 수 없습니다. : {0}"),
    INVALID_INVITATION_CODE(HttpStatus.BAD_REQUEST, "ERR_GROUP_003", "유효하지 않은 초대 코드입니다. : {0}"),
    GROUP_MEMBER_CAPACITY_REACHED(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_004",
            "그룹 최대 인원 수를 초과할 수 없습니다. (그룹 ID : {0})"
    ),

    /**
     * - {0} : maxMemberCount (요청한 최대 인원 수)
     * - {1} : totalMemberCount (현재 그룹 인원 수)
     */
    GROUP_MAX_COUNT_BELOW_CURRENT(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_005",
            "현재 인원 수({1})보다 작은 최대 인원 수({0})로 변경할 수 없습니다."
    ),

    /**
     * - {0} : memberId (요청한 사용자 ID)
     * - {1} : groupId (그룹 ID)
     */
    GROUP_UPDATE_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "ERR_GROUP_006",
            "그룹 정보를 수정할 권한이 없습니다. 요청자(memberId: {0})는 그룹(groupId: {1})의 소유자가 아닙니다."
    ),

    // group member
    /**
     * - {0} : memberId
     * - {1} : groupId
     */
    ALREADY_JOINED_GROUP(HttpStatus.BAD_REQUEST, "ERR_GROUP_MEMBER_001", "회원 {0}은 이미 그룹 {1}에 가입되어 있습니다."),
    /**
     * - {0} : memberId
     */
    GROUP_MEMBER_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_MEMBER_002",
            "해당 사용자의 그룹 참여 정보를 찾을 수 없습니다. : {0}"
    ),
    GROUP_MEMBER_NICKNAME_NOT_BLANK(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_MEMBER_003",
            "그룹 참여자의 닉네임은 비어있을 수 없습니다."
    ),
    /**
     * - {0} : groupMemberId
     */
    GROUP_MEMBER_ALREADY_LEFT(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_MEMBER_004",
            "해당 GroupMember({0})는 이미 그룹을 탈퇴한 상태입니다."
    ),
    /**
     * - {0} : GroupMemberId
     * - {1} : MemberId
     */
    GROUP_MEMBER_DOES_NOT_BELONG_TO_MEMBER(
            HttpStatus.FORBIDDEN,
            "ERR_GROUP_MEMBER_005",
            "요청한 Member({1})가 참여한 GroupMember({0})가 아닙니다."
    ),
    /**
     * - {0} : groupId
     */
    GROUP_OWNER_CANNOT_LEAVE(
            HttpStatus.BAD_REQUEST,
            "ERR_GROUP_MEMBER_006",
            "그룹({0})의 소유자는 나갈 수 없습니다."
    ),
    /**
     * - {0} : groupMemberId
     * - {1} : groupId
     */
    GROUP_MEMBER_NOT_BELONG_TO_GROUP(
        HttpStatus.FORBIDDEN,
        "ERR_GROUP_MEMBER_007",
        "GroupMember({0})는 그룹({1})에 속해있지 않습니다."
    ),
    /**
     * - {0} : groupMemberId
     * - {1} : groupId
     */
    GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID(
        HttpStatus.BAD_REQUEST,
        "ERR_GROUP_MEMBER_008",
        "해당 그룹({1})에 속하는 멤버({0})를 찾을 수 없습니다."
    ),
    GROUP_MEMBER_NOT_FOUND_BY_ID(
        HttpStatus.BAD_REQUEST,
        "ERR_GROUP_MEMBER_009",
        "해당 ID의 그룹 멤버를 찾을 수 없습니다. : {0}"
    ),

    // bungae
    /**
     * - {0} : bungaeId
     */
    BUNGAE_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_001", "해당 ID의 번개를 찾을 수 없습니다. : {0}"),
    /**
     * - {0} : bungaeId
     */
    BUNGAE_VOTE_UNAVAILABLE(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_002", "번개({0})에 대한 투표가 불가능합니다."),
    BUNGAE_VOTE_CONCURRENCY_LOCK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_BUNGAE_003", "번개 날짜 투표 동시성 락 획득에 실패했습니다."),
    BUNGAE_DATE_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_004", "번개({0})의 해당 날짜 후보({1})를 찾을 수 없습니다."),
    ALREADY_VOTED_FOR_BUNGAE_DATE(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_005", "이미 해당 날짜({1})에 투표했습니다. (번개 ID: {0})"),
    INVALID_BUNGAE_STATUS_FOR_DATE_CONFIRMATION(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_006", "번개({0})의 상태({1})로 인해 날짜 확정이 불가능합니다."),
    INVALID_ATTENDEE_LIMITS(HttpStatus.BAD_REQUEST, "ERR_BUNGAE_007", "번개({0})의 최소 인원({1})은 최대 인원({2})보다 클 수 없습니다."),

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


    // S3
    S3_FILE_UPLOAD_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_S3_001", "S3 File Upload 중 에러가 발생했습니다."),
    /**
     * - {0} : 파일 포맷
     */
    S3_FILE_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "ERR_S3_002", "파일 포맷에 문제가 있습니다. : {0}"),
    S3_FILE_MAX_SIZE_EXCEPTION(HttpStatus.BAD_REQUEST, "ERR_S3_003", "파일 크기가 3MB를 초과할 수 없습니다."),


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
    GLOBAL_NO_UPDATE_FIELD_PROVIDED(
            HttpStatus.BAD_REQUEST,
            "ERR_GLOBAL_004",
            "수정할 항목이 없습니다."
    ),
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