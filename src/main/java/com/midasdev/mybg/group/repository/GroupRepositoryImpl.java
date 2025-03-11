package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.QGroup;
import com.midasdev.mybg.group.domain.QGroupStatistics;
import com.midasdev.mybg.group_member.domain.QGroupMember;
import com.midasdev.mybg.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Group> findById(Long groupId) {
        QGroup group = QGroup.group;
        QMember member = QMember.member;

        Group result = jpaQueryFactory.selectFrom(group)
                                      .join(group.owner, member)
                                      .fetchJoin()
                                      .where(group.id.eq(groupId))
                                      .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Group> findWithStatisticsById(Long groupId) {
        QGroup group = QGroup.group;
        QGroupStatistics groupStatistics = QGroupStatistics.groupStatistics;
        QMember member = QMember.member;

        Group result = getGroupWithStatisticsQuery(group, member, groupStatistics)
                .where(group.id.eq(groupId)
                               .and(group.deleted.isFalse()))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private JPAQuery<Group> getGroupWithStatisticsQuery(QGroup group, QMember member, QGroupStatistics groupStatistics) {
        return jpaQueryFactory.selectFrom(group)
                              .join(group.owner, member)
                              .fetchJoin()
                              .leftJoin(group.groupStatistics, groupStatistics)
                              .fetchJoin();
    }

    @Override
    public List<Group> findGroupsWithStatisticsByMemberId(Long memberId) {
        QGroup group = QGroup.group;
        QGroupStatistics groupStatistics = QGroupStatistics.groupStatistics;
        QGroupMember groupMember = QGroupMember.groupMember;
        QMember member = QMember.member;

        return jpaQueryFactory.selectFrom(group)
                              .join(groupMember).on(group.eq(groupMember.group))
                              .fetchJoin()
                              .join(group.owner, member)
                              .fetchJoin()
                              .leftJoin(group.groupStatistics, groupStatistics)
                              .fetchJoin()
                              .where(groupMember.member.id.eq(memberId))
                              .fetch()
                              .stream().toList();
    }

    @Override
    public Optional<Group> findByInvitationCode(String invitationCode) {
        QGroup group = QGroup.group;
        QGroupStatistics groupStatistics = QGroupStatistics.groupStatistics;
        QMember member = QMember.member;

        Group result = getGroupWithStatisticsQuery(group, member, groupStatistics)
                .where(group.invitationCode.eq(invitationCode)
                                           .and(group.deleted.isFalse()))
                .fetchOne();
        return Optional.ofNullable(result);

    }

}
