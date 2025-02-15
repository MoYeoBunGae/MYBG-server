package com.midasdev.mybg.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenReIssueResponse(Long memberId, String accessToken, String refreshToken) {

}
