package com.midasdev.mybg.group.fixture;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.member.domain.Member;

public class GroupFixture {
    public static Group create(Member owner) {
        return Group.builder()
                .name("테스트 그룹")
                .profileImageUrl("http://test.com/group.png")
                .invitationCode("INVITE123")
                .maxMemberCount(100)
                .owner(owner)
                .deleted(false)
                .build();
    }
}
