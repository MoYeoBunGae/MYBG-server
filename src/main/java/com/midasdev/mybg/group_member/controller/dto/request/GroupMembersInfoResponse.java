package com.midasdev.mybg.group_member.controller.dto.request;

import com.midasdev.mybg.group_member.domain.GroupMember;
import java.util.List;
import lombok.Builder;

@Builder
public record GroupMembersInfoResponse(
        Long groupId,
        GroupMemberSimpleResponse owner,
        List<GroupMemberSimpleResponse> members
) {
    public static GroupMembersInfoResponse of(Long groupId,
                                              GroupMember owner,
                                              List<GroupMember> members) {
        return GroupMembersInfoResponse.builder()
                                       .groupId(groupId)
                                       .owner(GroupMemberSimpleResponse.from(owner))
                                       .members(members.stream()
                                                       .map(GroupMemberSimpleResponse::from)
                                                       .toList())
                                       .build();
    }
}
