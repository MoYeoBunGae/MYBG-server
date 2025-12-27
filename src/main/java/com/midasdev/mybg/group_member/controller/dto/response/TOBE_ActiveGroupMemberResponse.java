package com.midasdev.mybg.group_member.controller.dto.response;

import com.midasdev.mybg.group_member.domain.GroupMember;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TOBE_ActiveGroupMemberResponse(
        Long id,
        String nickname,
        LocalDateTime createdAt,
        Long memberId,
        Long groupId,
        String memberProfileImageUrl) {

    public static TOBE_ActiveGroupMemberResponse from(GroupMember groupMember) {
        return TOBE_ActiveGroupMemberResponse.builder()
                .id(groupMember.getId())
                .nickname(groupMember.getNickname())
                .createdAt(groupMember.getAudit().getCreatedAt())
                .memberId(groupMember.getMember().getId())
                .groupId(groupMember.getGroup().getId())
                .memberProfileImageUrl(groupMember.getMemberProfileImageUrl())
                .build();
    }
}
