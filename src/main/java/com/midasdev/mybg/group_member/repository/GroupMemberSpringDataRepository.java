package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberSpringDataRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByMemberAndGroup(Member member, Group group);

    Integer countByGroup(Group group);
}
