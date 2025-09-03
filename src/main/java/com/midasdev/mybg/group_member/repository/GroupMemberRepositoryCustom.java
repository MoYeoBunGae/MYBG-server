package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.member.domain.Member;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepositoryCustom {

    List<GroupMember> findAllActiveGroupMembersExceptOwner(Long groupId);

    Optional<GroupMember> findByMemberAndGroup(Member member, Group group);

}
