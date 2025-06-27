package com.midasdev.mybg.group_member.fixture;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.member.domain.Member;

public class GroupMemberFixture {
    public static GroupMember create(Group group, Member member) {
        return GroupMember.builder()
                .nickname("호스트")
                .group(group)
                .member(member)
                .memberProfileImageUrl(member.getProfileImageUrl())
                .deleted(false)
                .build();
    }
}
