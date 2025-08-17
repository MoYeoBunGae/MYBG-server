package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.domain.QBungae;
import com.midasdev.mybg.bungae.domain.QBungaeAttendee;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.repository.dto.QBungaeDto;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomBungaeRepositoryImpl implements CustomBungaeRepository {

    private final JPAQueryFactory queryFactory;

    private static final QBungae bungae = QBungae.bungae;
    private static final QBungaeAttendee attendee = QBungaeAttendee.bungaeAttendee;

    public CursorPage<Bungae> findAllByAttendeeMemberIdAndStatusIn(
            Long memberId,
            List<BungaeStatus> statuses,
            CursorPageable cursorPageable
    ) {
        Long cursorId = cursorPageable.lastCursorId();
        int pageSize = cursorPageable.pageSize();

        BooleanExpression condition = Expressions.allOf(
                attendee.groupMember.member.id.eq(memberId)
                                              .and(attendee.deleted.isFalse())
                                              .and(bungae.deleted.isFalse()),
                bungaeStatuesAndCursorCondition(cursorId, statuses)
        );

        List<Bungae> fetchedContent = queryFactory
                .selectFrom(bungae)
                .join(attendee).on(attendee.bungae.eq(bungae))
                .fetchJoin()
                .join(bungae.group).fetchJoin()
                .join(bungae.host).fetchJoin()
                .where(condition)
                .orderBy(bungae.id.desc()) // id 기준 내림차순 정렬 (최신순)
                .limit(cursorPageable.getFetchSize())
                .fetch();

        return new CursorPage<>(fetchedContent, pageSize);
    }

    @Override
    public CursorPage<BungaeDto> findByGroupIdAndStatusIn(Long groupId, List<BungaeStatus> statuses, CursorPageable cursorPageable) {
        Long cursorId = cursorPageable.lastCursorId();
        int pageSize = cursorPageable.pageSize();

        BooleanExpression condition = Expressions.allOf(
                bungae.group.id.eq(groupId)
                               .and(bungae.deleted.isFalse()),
                bungaeStatuesAndCursorCondition(cursorId, statuses)
        );

        JPQLQuery<Integer> attendeeCount = JPAExpressions
                .select(attendee.count().intValue())
                .from(attendee)
                .where(attendee.bungae.eq(bungae)
                                      .and(attendee.deleted.isFalse()));

        List<BungaeDto> fetchedContent = queryFactory
                .select(new QBungaeDto(
                        bungae.id,
                        bungae.name,
                        bungae.description,
                        bungae.minAttendees,
                        bungae.maxAttendees,
                        bungae.isOnline,
                        bungae.location,
                        bungae.bungaeDateTime,
                        bungae.dateVoteClosedAt,
                        bungae.status,
                        bungae.audit.createdAt,
                        bungae.deleted,
                        bungae.group.id,
                        bungae.host.id,
                        attendeeCount
                ))
                .from(bungae)
                .join(bungae.group)
                .join(bungae.host)
                .where(condition) // 그룹 ID, 상태 조건, last cursor ID 조건
                .orderBy(bungae.id.desc())
                .limit(cursorPageable.getFetchSize())
                .fetch();

        return new CursorPage<>(fetchedContent, pageSize);
    }

    private BooleanExpression bungaeStatuesAndCursorCondition(Long cursorId, List<BungaeStatus> statuses) {
        BooleanExpression statusCondition = (statuses == null || statuses.isEmpty())
                ? null
                : bungae.status.in(statuses);

        return Expressions.allOf(
                statusCondition,
                cursorId != null ? bungae.id.lt(cursorId) : null
        );
    }

}
