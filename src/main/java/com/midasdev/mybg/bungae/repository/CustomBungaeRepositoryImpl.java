package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.domain.QBungae;
import com.midasdev.mybg.bungae.domain.QBungaeAttendee;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.querydsl.core.types.dsl.BooleanExpression;
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

        BooleanExpression condition = createMemberAndBungaeStatusCondition(memberId, statuses)
            .and(cursorId != null ? bungae.id.lt(cursorId) : null); // 내림차순 정렬이므로 lt

        List<Bungae> content = queryFactory
                .selectFrom(bungae)
                .join(attendee).on(attendee.bungae.eq(bungae))
                .fetchJoin()
                .join(bungae.group).fetchJoin()
                .join(bungae.host).fetchJoin()
                .where(condition)
                .orderBy(bungae.id.desc()) // id 기준 내림차순 정렬 (최신순)
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        Long nextCursorId = null;
        if (hasNext) {
            Bungae last = content.remove(pageSize);
            nextCursorId = last.getId();
        }

        return new CursorPage<>(content, nextCursorId, hasNext);
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
