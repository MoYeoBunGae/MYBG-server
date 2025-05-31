package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.domain.QGroupMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QGroupMember groupMember = QGroupMember.groupMember;

    @Override
    public List<GroupMember> findAllActiveGroupMembersExceptOwner(Long groupId) {
        return queryFactory.selectFrom(groupMember)
                           .where(
                                   groupMember.group.id.eq(groupId),
                                   groupMember.deleted.isFalse()
                           )
                           .orderBy(groupMember.audit.createdAt.asc())
                           .fetch();
    }

}

