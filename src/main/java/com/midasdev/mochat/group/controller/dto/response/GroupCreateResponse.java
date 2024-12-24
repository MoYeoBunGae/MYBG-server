package com.midasdev.mochat.group.controller.dto.response;


import com.midasdev.mochat.group.domain.Group;
import lombok.Builder;

@Builder
public record GroupCreateResponse(long groupId, String name, String profileImageUrl, String invitationCode) {

    public static GroupCreateResponse from(Group group) {
        return GroupCreateResponse.builder()
                .groupId(group.getId())
                .name(group.getName())
                .profileImageUrl(group.getProfileImageUrl())
                .invitationCode(group.getInvitationCode())
                .build();
    }

}
