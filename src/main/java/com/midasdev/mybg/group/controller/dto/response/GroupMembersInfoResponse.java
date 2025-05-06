package com.midasdev.mybg.group.controller.dto.response;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.controller.dto.response.GroupMemberSimpleResponse;
import com.midasdev.mybg.group_member.domain.GroupMember;
import java.util.List;
import lombok.Builder;

@Builder
public record GroupMembersInfoResponse(
        Long groupId,
        GroupMemberSimpleResponse owner,
        List<GroupMemberSimpleResponse> members
) {
    public static GroupMembersInfoResponse from(Group group, List<GroupMember> members) {
        GroupMemberSimpleResponse owner = members.stream()
                                                 .filter(m -> group.isOwnedBy(m.getMember()))
                                                 .findFirst()
                                                 .map(GroupMemberSimpleResponse::from)
                                                 .orElseThrow(() -> new ApplicationException(
                                                         ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND, group.getOwner().getId()));

        List<GroupMemberSimpleResponse> others = members.stream()
                                                        .filter(m -> !group.isOwnedBy(m.getMember()))
                                                        .map(GroupMemberSimpleResponse::from)
                                                        .toList();

        return GroupMembersInfoResponse.builder()
                                       .groupId(group.getId())
                                       .owner(owner)
                                       .members(others)
                                       .build();
    }
}
