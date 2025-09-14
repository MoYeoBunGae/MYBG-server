package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group_member.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>, GroupMemberRepositoryCustom  {

    boolean existsByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long id);

}