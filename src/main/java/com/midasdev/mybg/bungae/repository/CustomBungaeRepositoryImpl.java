package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.domain.QBungae;
import com.midasdev.mybg.bungae.domain.QBungaeAttendee;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.repository.dto.QBungaeDto;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.querydsl.core.types.Expression;
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

    public CursorPage<BungaeDto> findAllByAttendeeMemberIdAndStatusIn(
            Long memberId, List<BungaeStatus> statuses, CursorPageable cursorPageable) {
        Long cursorId = cursorPageable.lastCursorId();
        int pageSize = cursorPageable.pageSize();

        BooleanExpression condition =
                Expressions.allOf(
                        attendee.groupMember
                                .member
                                .id
                                .eq(memberId)
                                .and(attendee.deleted.isFalse())
                                .and(bungae.deleted.isFalse()),
                        bungaeStatuesAndCursorCondition(cursorId, statuses));

        List<BungaeDto> fetchedContent =
                queryFactory
                        .select(createBungaeDtoProjection())
                        .from(bungae)
                        .join(attendee)
                        .on(attendee.bungae.eq(bungae))
                        .join(bungae.group)
                        .join(bungae.host)
                        .where(condition)
                        .orderBy(bungae.id.desc())
                        .limit(cursorPageable.getFetchSize())
                        .fetch();

        return new CursorPage<>(fetchedContent, pageSize);
    }

    @Override
    public CursorPage<BungaeDto> findByGroupIdAndStatusIn(
            Long groupId, List<BungaeStatus> statuses, CursorPageable cursorPageable) {
        Long cursorId = cursorPageable.lastCursorId();
        int pageSize = cursorPageable.pageSize();

        BooleanExpression condition =
                Expressions.allOf(
                        bungae.group.id.eq(groupId).and(bungae.deleted.isFalse()),
                        bungaeStatuesAndCursorCondition(cursorId, statuses));

        List<BungaeDto> fetchedContent =
                queryFactory
                        .select(createBungaeDtoProjection())
                        .from(bungae)
                        .join(bungae.group)
                        .join(bungae.host)
                        .where(condition)
                        .orderBy(bungae.id.desc())
                        .limit(cursorPageable.getFetchSize())
                        .fetch();

        return new CursorPage<>(fetchedContent, pageSize);
    }

    private BooleanExpression bungaeStatuesAndCursorCondition(
            Long cursorId, List<BungaeStatus> statuses) {
        BooleanExpression statusCondition =
                (statuses == null || statuses.isEmpty()) ? null : bungae.status.in(statuses);

        return Expressions.allOf(statusCondition, cursorId != null ? bungae.id.lt(cursorId) : null);
    }

    private Expression<BungaeDto> createBungaeDtoProjection() {
        return new QBungaeDto(
                bungae.id,
                bungae.name,
                bungae.description,
                bungae.minAttendees,
                bungae.maxAttendees,
                bungae.isOnline,
                bungae.location,
                bungae.bungaeDateTime.date,
                bungae.bungaeDateTime.time,
                bungae.dateVoteClosedAt,
                bungae.status,
                bungae.audit.createdAt,
                bungae.deleted,
                bungae.group.id,
                bungae.host.id,
                createAttendeeCountSubQuery());
    }

    private JPQLQuery<Integer> createAttendeeCountSubQuery() {
        return JPAExpressions.select(attendee.count().intValue())
                .from(attendee)
                .where(attendee.bungae.eq(bungae).and(attendee.deleted.isFalse()));
    }
}
