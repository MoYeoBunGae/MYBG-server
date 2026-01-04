package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.domain.QBungae;
import com.midasdev.mybg.bungae.domain.QBungaeAttendee;
import com.midasdev.mybg.bungae.domain.QBungaeDateVote;
import com.midasdev.mybg.bungae.domain.QBungaeRecruitDateOption;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.repository.dto.QBungaeDto;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.assertion.Assertion;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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
            Long groupId,
            List<BungaeStatus> statuses,
            CursorPageable cursorPageable,
            Long memberId) {
        Long cursorId = cursorPageable.lastCursorId();
        int pageSize = cursorPageable.pageSize();

        BooleanExpression condition =
                Expressions.allOf(
                        bungae.group.id.eq(groupId).and(bungae.deleted.isFalse()),
                        bungaeStatuesAndCursorCondition(cursorId, statuses));

        List<BungaeDto> fetchedContent =
                queryFactory
                        .select(createBungaeDtoProjection(memberId))
                        .from(bungae)
                        .join(bungae.group)
                        .join(bungae.host)
                        .where(condition)
                        .orderBy(bungae.id.desc())
                        .limit(cursorPageable.getFetchSize())
                        .fetch();

        return new CursorPage<>(fetchedContent, pageSize);
    }

    @Override
    public Optional<BungaeDto> findBungaeDtoById(Long bungaeId, Long memberId) {
        BungaeDto result =
                queryFactory
                        .select(createBungaeDtoProjection(memberId))
                        .from(bungae)
                        .join(bungae.group)
                        .join(bungae.host)
                        .where(bungae.id.eq(bungaeId).and(bungae.deleted.isFalse()))
                        .fetchOne();

        return Optional.ofNullable(result);
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
                createAttendeeCountSubQuery(),
                Expressions.nullExpression(Boolean.class),
                Expressions.nullExpression(Boolean.class));
    }

    private Expression<BungaeDto> createBungaeDtoProjection(Long memberId) {
        Assertion.with(memberId)
                .setValidation(id -> id != null)
                .validateOrThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR,
                                        "MemberId for BungaeDto projection cannot be null"));

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
                createAttendeeCountSubQuery(),
                createHasJoinedSubQuery(memberId),
                createHasVotedSubQuery(memberId));
    }

    private JPQLQuery<Integer> createAttendeeCountSubQuery() {
        return JPAExpressions.select(attendee.count().intValue())
                .from(attendee)
                .where(attendee.bungae.eq(bungae).and(attendee.deleted.isFalse()));
    }

    private Expression<Boolean> createHasJoinedSubQuery(Long memberId) {
        QBungaeAttendee subAttendee = new QBungaeAttendee("subAttendee");
        return JPAExpressions.selectOne()
                .from(subAttendee)
                .where(
                        subAttendee.bungae.eq(bungae),
                        subAttendee.groupMember.member.id.eq(memberId),
                        subAttendee.deleted.isFalse())
                .exists();
    }

    private Expression<Boolean> createHasVotedSubQuery(Long memberId) {
        QBungaeDateVote bungaeDateVote = QBungaeDateVote.bungaeDateVote;
        QBungaeRecruitDateOption dateOption = QBungaeRecruitDateOption.bungaeRecruitDateOption;
        return JPAExpressions.selectOne()
                .from(bungaeDateVote)
                .join(bungaeDateVote.dateOption, dateOption)
                .where(dateOption.bungae.eq(bungae), bungaeDateVote.voter.member.id.eq(memberId))
                .exists();
    }
}
