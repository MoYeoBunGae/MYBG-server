package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.domain.QBungae;
import com.midasdev.mybg.bungae.domain.QBungaeAttendee;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.BooleanExpression;

@RequiredArgsConstructor
public class CustomBungaeRepositoryImpl implements CustomBungaeRepository {

    private final JPAQueryFactory queryFactory;

    private static final QBungae bungae = QBungae.bungae;
    private static final QBungaeAttendee attendee = QBungaeAttendee.bungaeAttendee;

    // TODO: cursor 방식으로 변경
    @Override
    public Page<Bungae> findAllByAttendeeMemberIdAndStatusIn(Long memberId, List<BungaeStatus> statuses, Pageable pageable) {
        BooleanExpression condition = createMemberAndBungaeStatusCondition(memberId, statuses);

        List<Bungae> content = queryFactory
                .selectFrom(bungae)
                .join(attendee).on(attendee.bungae.eq(bungae))
                .fetchJoin()
                .join(bungae.group).fetchJoin()
                .join(bungae.host).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = Optional.ofNullable(
                queryFactory
                    .select(bungae.count())
                    .from(bungae)
                    .join(attendee).on(attendee.bungae.eq(bungae))
                    .where(condition)
                    .fetchOne()
            ).orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR, "쿼리 결과 오류"));

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression createMemberAndBungaeStatusCondition(Long memberId, List<BungaeStatus> statuses) {
        BooleanExpression baseCondition = attendee.groupMember.member.id.eq(memberId)
                .and(attendee.deleted.isFalse())
                .and(bungae.deleted.isFalse());

        BooleanExpression statusCondition = (statuses == null || statuses.isEmpty())
                ? null
                : bungae.status.in(statuses);

        return (statusCondition == null)
                ? baseCondition
                : baseCondition.and(statusCondition);
    }
}
