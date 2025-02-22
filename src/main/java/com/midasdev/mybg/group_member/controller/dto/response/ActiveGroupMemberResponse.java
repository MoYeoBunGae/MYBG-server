package com.midasdev.mybg.group_member.controller.dto.response;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.member.domain.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ActiveGroupMemberResponse(
        Long id,
        String nickname,
        LocalDateTime createdAt,
        Member member,
        Group group
) {

    public static ActiveGroupMemberResponse from(GroupMember groupMember) {
        return ActiveGroupMemberResponse.builder()
                                        .id(groupMember.getId())
                                        .nickname(groupMember.getNickname())
                                        .createdAt(groupMember.getAudit().getCreatedAt())
                                        .member(groupMember.getMember())
                                        .group(groupMember.getGroup())
                                        .build();
    }
}
