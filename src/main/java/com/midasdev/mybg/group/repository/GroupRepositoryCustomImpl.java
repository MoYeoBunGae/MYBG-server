package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.QGroup;
import com.midasdev.mybg.group_member.domain.QGroupMember;
import com.midasdev.mybg.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Group> findGroupsByMemberId(Long memberId) {
        QGroup group = QGroup.group;
        QGroupMember groupMember = QGroupMember.groupMember;
        QMember member = QMember.member;

        return jpaQueryFactory.selectFrom(group)
                              .join(groupMember).on(group.eq(groupMember.group))
                              .fetchJoin()
                              .join(group.owner, member)
                              .fetchJoin()
                              .where(groupMember.member.id.eq(memberId))
                              .fetch()
                              .stream().toList();
    }

    @Override
    public Optional<Group> findByInvitationCode(String invitationCode) {
        QGroup group = QGroup.group;
        QMember member = QMember.member;

        Group result = jpaQueryFactory.selectFrom(group)
                                      .join(group.owner, member)
                                      .fetchJoin()
                                      .where(group.invitationCode.eq(invitationCode)
                                                                 .and(group.deleted.isFalse()))
                                      .fetchOne();
        return Optional.ofNullable(result);

    }

}
