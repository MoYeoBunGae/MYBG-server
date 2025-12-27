package com.midasdev.mybg.group_member.controller.dto.response;

import com.midasdev.mybg.group_member.domain.GroupMember;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GroupMemberSimpleResponse(
        Long id, Long memberId, String nickname, String profileImageUrl, LocalDateTime joinedAt) {
    public static GroupMemberSimpleResponse from(GroupMember member) {
        return GroupMemberSimpleResponse.builder()
                .id(member.getId())
                .memberId(member.getMember().getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getMember().getProfileImageUrl())
                .joinedAt(member.getAudit().getCreatedAt())
                .build();
    }
}
