package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.config.QueryDslConfig;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import com.midasdev.mybg.member.repository.MemberRepository;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
class CustomBungaeRepositoryTest {

    @Autowired
    private BungaeRepository bungaeRepository;

    @Autowired
    private BungaeAttendeeRepository bungaeAttendeeRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Group group;
    private GroupMember groupMember;
    private List<Bungae> savedBungaes;

    @BeforeEach
    void setUp() {
        // 기본 엔티티 생성 및 저장
        member = memberRepository.save(MemberFixture.create());
        group = groupRepository.save(GroupFixture.create(member));
        groupMember = groupMemberRepository.save(GroupMemberFixture.create(group, member));

        // 각 BungaeStatus별로 Bungae 생성 및 저장
        savedBungaes = new ArrayList<>();

        // DATE_VOTING 번개 생성
        Bungae dateVotingBungae = bungaeRepository.save(
                BungaeFixture.createWithDateVoting(group, groupMember));
        savedBungaes.add(dateVotingBungae);

        // RECRUITING 번개 생성
        Bungae recruitingBungae = bungaeRepository.save(
                BungaeFixture.createWithRecruiting(group, groupMember));
        savedBungaes.add(recruitingBungae);

        // RECRUITING_CLOSED 번개 생성
        Bungae recruitingClosedBungae = bungaeRepository.save(
                BungaeFixture.createWithRecruitingClosed(group, groupMember));
        savedBungaes.add(recruitingClosedBungae);

        // CLOSED 번개 생성
        Bungae closedBungae = bungaeRepository.save(
                BungaeFixture.createWithClosed(group, groupMember));
        savedBungaes.add(closedBungae);

        // CANCELLED 번개 생성
        Bungae cancelledBungae = bungaeRepository.save(
                BungaeFixture.createWithCancelled(group, groupMember));
        savedBungaes.add(cancelledBungae);

        // 각 번개에 대해 BungaeAttendee 생성 (member가 참여자로 등록)
        for (Bungae bungae : savedBungaes) {
            BungaeAttendee attendee = BungaeAttendee.builder()
                    .bungae(bungae)
                    .groupMember(groupMember)
                    .deleted(false)
                    .build();
            bungaeAttendeeRepository.save(attendee);
        }
    }

    @Test
    @DisplayName("lastCursorId, size 조건에 맞는 번개만 조회된다")
    void findAllByAttendeeMemberIdAndStatusIn_withCursorAndSize_shouldReturnCorrectBungaes() {
        // given
        Long lastCursorId = Long.MAX_VALUE; // 마지막에 저장된 번개의 ID보다 큰 값
        int pageSize = 2;
        CursorPageable cursorPageable = new CursorPageable(lastCursorId, pageSize);

        // when
        CursorPage<Bungae> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null, // 모든 상태
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getContent())
                .extracting(Bungae::getId)
                .allMatch(id -> id < lastCursorId); // cursor보다 작은 ID만 조회되어야 함
        assertThat(result.isHasNext()).isTrue();
        // nextCursorId는 반환된 마지막 요소의 ID여야 함
        Long expectedNextCursorId = result.getContent().get(pageSize - 1).getId();
        assertThat(result.getNextCursorId()).isEqualTo(expectedNextCursorId);
    }

    @Test
    @DisplayName("특정 status 조건에 맞는 번개만 조회된다")
    void findAllByAttendeeMemberIdAndStatusIn_withStatus_shouldReturnFilteredBungaes() {
        // given
        List<BungaeStatus> targetStatuses = List.of(BungaeStatus.DATE_VOTING, BungaeStatus.RECRUITING);
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<Bungae> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                targetStatuses,
                cursorPageable
        );

        // then
        assertThat(result.getContent())
                .extracting(Bungae::getStatus)
                .containsExactlyInAnyOrder(BungaeStatus.DATE_VOTING, BungaeStatus.RECRUITING);
        assertThat(result.getContent())
                .extracting(Bungae::getStatus)
                .doesNotContain(BungaeStatus.RECRUITING_CLOSED, BungaeStatus.CLOSED, BungaeStatus.CANCELLED);
    }

    @Test
    @DisplayName("status가 null일 경우 모든 상태의 번개가 조회된다")
    void findAllByAttendeeMemberIdAndStatusIn_withNullStatus_shouldReturnAllBungaes() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<Bungae> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null, // status가 null
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(5); // 모든 상태의 번개 5개 조회
        assertThat(result.getContent())
                .extracting(Bungae::getStatus)
                .containsExactlyInAnyOrder(
                        BungaeStatus.DATE_VOTING,
                        BungaeStatus.RECRUITING,
                        BungaeStatus.RECRUITING_CLOSED,
                        BungaeStatus.CLOSED,
                        BungaeStatus.CANCELLED
                );
    }

    @Test
    @DisplayName("lastCursorId가 null일 경우 가장 최근 것부터 조회된다")
    void findAllByAttendeeMemberIdAndStatusIn_withNullCursor_shouldReturnFromLatest() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 3); // lastCursorId가 null

        // when
        CursorPage<Bungae> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(3);

        // ID 기준 내림차순 정렬 확인 (최신순)
        List<Long> resultIds = result.getContent().stream()
                .map(Bungae::getId)
                .toList();

        List<Long> sortedIds = savedBungaes.stream()
                .map(Bungae::getId)
                .sorted((a, b) -> Long.compare(b, a)) // 내림차순 정렬
                .limit(3)
                .toList();

        assertThat(resultIds).isEqualTo(sortedIds);
        // nextCursorId는 반환된 마지막 요소의 ID여야 함
        assertThat(result.getNextCursorId()).isEqualTo(result.getContent().get(2).getId());
    }
}
