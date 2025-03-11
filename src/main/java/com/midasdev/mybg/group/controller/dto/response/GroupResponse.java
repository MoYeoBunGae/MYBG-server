package com.midasdev.mybg.group.controller.dto.response;


import com.midasdev.mybg.group.domain.Group;
import lombok.Builder;

@Builder
public record GroupResponse(long groupId, String name, String profileImageUrl, String invitationCode, int totalMemberCount) {

    public static GroupResponse from(Group group) {
        return GroupResponse.builder()
                            .groupId(group.getId())
                            .name(group.getName())
                            .profileImageUrl(group.getProfileImageUrl())
                            .invitationCode(group.getInvitationCode())
                            .totalMemberCount(group.getTotalMemberCount())
                            .build();
    }
}
