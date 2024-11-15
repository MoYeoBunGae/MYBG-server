package com.midasdev.mochat.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenReIssueResponse(Long memberId, String accessToken, String refreshToken) {

}
