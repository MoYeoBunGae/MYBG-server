package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group_member.domain.GroupMember;
import java.util.List;

public interface GroupMemberRepositoryCustom {

    List<GroupMember> findAllActiveGroupMembersExceptOwner(Long groupId);

}
