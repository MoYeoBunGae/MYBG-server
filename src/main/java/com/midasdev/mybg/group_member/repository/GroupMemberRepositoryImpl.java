package com.midasdev.mybg.group_member.repository;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.QGroup;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.domain.QGroupMember;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QGroupMember groupMember = QGroupMember.groupMember;
    private final QGroup group = QGroup.group;
    private final QMember member = QMember.member;

    @Override
    public List<GroupMember> findAllActiveGroupMembersExceptOwner(Long groupId) {
        return queryFactory
                .selectFrom(groupMember)
                .where(groupMember.group.id.eq(groupId), groupMember.deleted.isFalse())
                .orderBy(groupMember.audit.createdAt.asc())
                .fetch();
    }

    @Override
    public Optional<GroupMember> findByMemberAndGroup(Member member, Group group) {
        GroupMember result =
                queryFactory
                        .selectFrom(groupMember)
                        .leftJoin(groupMember.member, this.member)
                        .fetchJoin()
                        .leftJoin(groupMember.group, this.group)
                        .fetchJoin()
                        .where(
                                groupMember.member.eq(member),
                                groupMember.group.eq(group),
                                groupMember.deleted.isFalse())
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<GroupMember> findEntityById(Long groupMemberId) {
        GroupMember result =
                queryFactory
                        .selectFrom(groupMember)
                        .leftJoin(groupMember.member, this.member)
                        .fetchJoin()
                        .leftJoin(groupMember.group, this.group)
                        .fetchJoin()
                        .where(groupMember.id.eq(groupMemberId), groupMember.deleted.isFalse())
                        .fetchOne();

        return Optional.ofNullable(result);
    }
}
