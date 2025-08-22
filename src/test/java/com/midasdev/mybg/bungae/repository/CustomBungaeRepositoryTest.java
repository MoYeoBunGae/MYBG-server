package com.midasdev.mybg.bungae.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.config.QueryDslConfig;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import com.midasdev.mybg.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * 공통 given
 * - Member: member, otherMember
 * - Group: group, otherGroup
 * - GroupMember:
 * * group : groupMember (member가 group에 참여자로 등록된 상태)
 * * otherGroup : otherGroupMember(otherMember), memberInOtherGroup(member)
 * - Bungae: savedBungaes (group에서 member가 참여자로 등록된, 각각의 상태를 가지는 번개들), otherMemberSavedBungaes (otherGroup에서 otherMember가 참여자로 등록된, 각각의 상태를 가지는 번개들)
 * + member가 otherGroup의 RECRUITING, CLOSED 번개에도 참여
 */

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

    private Member member, otherMember;
    private Group group, otherGroup;
    private GroupMember groupMember;
    private GroupMember otherGroupMember;
    private GroupMember memberInOtherGroup;
    private List<Bungae> groupSavedBungaes;
    private List<Long> memberJoinedBungaeIds;

    @BeforeEach
    void setUp() {
        // 기본 엔티티 생성 및 저장
        member = memberRepository.save(MemberFixture.create());
        otherMember = memberRepository.save(MemberFixture.createSecondMember());
        group = groupRepository.save(GroupFixture.create(member));
        otherGroup = groupRepository.save(GroupFixture.create(otherMember, "다른 그룹", "other-group-invitation-code"));
        groupMember = groupMemberRepository.save(GroupMemberFixture.create(group, member));
        otherGroupMember = groupMemberRepository.save(GroupMemberFixture.create(otherGroup, otherMember));
        memberInOtherGroup = groupMemberRepository.save(GroupMemberFixture.create(otherGroup, member));

        // 각 BungaeStatus별로 Bungae 생성 및 저장
        groupSavedBungaes = new ArrayList<>();
        memberJoinedBungaeIds = new ArrayList<>();

        // 각 상태별 번개 생성 및 참여자 등록 - group
        createBungaeAndSaveAttendee(BungaeFixture::createWithDateVoting, group, groupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithRecruiting, group, groupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithRecruitingClosed, group, groupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithClosed, group, groupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithCancelled, group, groupMember);

        //  각 상태별 번개 생성 및 참여자 등록 - otherGroup
        createBungaeAndSaveAttendee(BungaeFixture::createWithDateVoting, otherGroup, otherGroupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithRecruiting, otherGroup, otherGroupMember, memberInOtherGroup);
        createBungaeAndSaveAttendee(BungaeFixture::createWithRecruitingClosed, otherGroup, otherGroupMember);
        createBungaeAndSaveAttendee(BungaeFixture::createWithClosed, otherGroup, otherGroupMember, memberInOtherGroup);
        createBungaeAndSaveAttendee(BungaeFixture::createWithCancelled, otherGroup, otherGroupMember);
    }

    /**
     * 번개 생성과 참여자 저장을 통합한 헬퍼 메서드
     */
    private void createBungaeAndSaveAttendee(
            BiFunction<Group, GroupMember, Bungae> bungaeCreator,
            Group group,
            GroupMember host,
            GroupMember... additionalAttendees
    ) {
        Bungae bungae = bungaeRepository.save(bungaeCreator.apply(group, host));

        if (group.getId().equals(this.group.getId())) {
            groupSavedBungaes.add(bungae);
        }

        saveAttendee(bungae, host);

        // host가 member인 경우 memberJoinedBungaeIds에 추가
        if (host.getMember().getId().equals(member.getId())) {
            memberJoinedBungaeIds.add(bungae.getId());
        }

        for (GroupMember attendee : additionalAttendees) {
            saveAttendee(bungae, attendee);

            // 추가 참여자가 member인 경우 memberJoinedBungaeIds에 추가
            if (attendee.getMember().getId().equals(member.getId())) {
                memberJoinedBungaeIds.add(bungae.getId());
            }
        }

    }

    private void saveAttendee(Bungae bungae, GroupMember groupMember) {
        BungaeAttendee attendee = BungaeAttendee.builder()
                                                .bungae(bungae)
                                                .groupMember(groupMember)
                                                .deleted(false)
                                                .build();
        bungaeAttendeeRepository.save(attendee);
    }

    @Test
    @DisplayName("B-2-R-1: 조건에 맞는 올바른 나의 번개 조회 - lastCursorId, size")
    void findAllByAttendeeMemberIdAndStatusIn_withCursorAndSize_shouldReturnCorrectBungaes() {
        // given
        Long lastCursorId = Long.MAX_VALUE;
        int pageSize = 2;
        CursorPageable cursorPageable = new CursorPageable(lastCursorId, pageSize);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getContent())
                .extracting(BungaeDto::id)
                .allMatch(id -> id < lastCursorId)
                .allMatch(memberJoinedBungaeIds::contains);
    }

    @Test
    @DisplayName("B-2-R-2: 조건에 맞는 올바른 나의 번개 조회 - status")
    void findAllByAttendeeMemberIdAndStatusIn_withStatus_shouldReturnFilteredBungaes() {
        // given
        List<BungaeStatus> targetStatuses = List.of(BungaeStatus.DATE_VOTING, BungaeStatus.RECRUITING);
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                targetStatuses,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(3); // DATE_VOTING 1개 + RECRUITING 2개
        assertThat(result.getContent())
                .extracting(BungaeDto::id)
                .allMatch(memberJoinedBungaeIds::contains);
        assertThat(result.getContent())
                .extracting(BungaeDto::status)
                .doesNotContain(BungaeStatus.RECRUITING_CLOSED, BungaeStatus.CLOSED, BungaeStatus.CANCELLED);
    }

    @Test
    @DisplayName("B-2-R-3: 조건에 맞는 올바른 나의 번개 조회 - status가 null일 경우")
    void findAllByAttendeeMemberIdAndStatusIn_withNullStatus_shouldReturnAllBungaes() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(memberJoinedBungaeIds.size()); // member가 참여한 모든 번개 수와 일치
        assertThat(result.getContent())
                .extracting(BungaeDto::id)
                .containsExactlyInAnyOrderElementsOf(memberJoinedBungaeIds); // member가 참여한 모든 번개 ID와 정확히 일치
        assertThat(result.getContent())
                .extracting(BungaeDto::status)
                .contains(
                        BungaeStatus.DATE_VOTING,
                        BungaeStatus.RECRUITING,
                        BungaeStatus.RECRUITING_CLOSED,
                        BungaeStatus.CLOSED,
                        BungaeStatus.CANCELLED
                );
    }

    @Test
    @DisplayName("B-2-R-4: lastCursorId가 null일 경우 가장 최근 것부터 나의 번개 조회")
    void findAllByAttendeeMemberIdAndStatusIn_withNullCursor_shouldReturnFromLatest() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 3);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(3);

        // ID 기준 내림차순 정렬 확인 (최신순)
        List<Long> resultIds = result.getContent().stream()
                                     .map(BungaeDto::id)
                                     .toList();

        // member가 참여한 번개 ID들을 내림차순으로 정렬하여 상위 3개와 비교
        List<Long> expectedSortedIds = memberJoinedBungaeIds.stream()
                                                            .sorted((a, b) -> Long.compare(b, a))
                                                            .limit(3)
                                                            .toList();

        assertThat(resultIds).isEqualTo(expectedSortedIds);
    }

    @Test
    @DisplayName("B-3-R-1: 조건에 맞는 올바른 그룹 번개 조회 - lastCursorId, size")
    void findByGroupIdAndStatusIn_withCursorAndSize_shouldReturnCorrectBungaes() {
        // given
        Long lastCursorId = Long.MAX_VALUE;
        int pageSize = 2;
        CursorPageable cursorPageable = new CursorPageable(lastCursorId, pageSize);
        List<BungaeStatus> statuses = List.of(BungaeStatus.RECRUITING, BungaeStatus.DATE_VOTING);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findByGroupIdAndStatusIn(
                group.getId(),
                statuses,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getContent())
                .extracting(BungaeDto::id)
                .allMatch(id -> id < lastCursorId);
        assertThat(result.getContent())
                .extracting(BungaeDto::groupId)
                .containsOnly(group.getId());
    }

    @Test
    @DisplayName("B-3-R-2: 조건에 맞는 올바른 그룹 번개 조회 - status")
    void findByGroupIdAndStatusIn_withStatus_shouldReturnFilteredBungaes() {
        // given
        List<BungaeStatus> targetStatuses = List.of(BungaeStatus.RECRUITING, BungaeStatus.CLOSED);
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findByGroupIdAndStatusIn(
                group.getId(),
                targetStatuses,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(BungaeDto::status)
                .doesNotContain(BungaeStatus.DATE_VOTING, BungaeStatus.RECRUITING_CLOSED, BungaeStatus.CANCELLED);
        assertThat(result.getContent())
                .extracting(BungaeDto::groupId)
                .containsOnly(group.getId());
    }

    @Test
    @DisplayName("B-3-R-3: 조건에 맞는 올바른 그룹 번개 조회 - status가 null일 경우")
    void findByGroupIdAndStatusIn_withNullStatus_shouldReturnAllBungaes() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 10);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findByGroupIdAndStatusIn(
                group.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent())
                .extracting(BungaeDto::groupId)
                .containsOnly(group.getId());
    }

    @Test
    @DisplayName("B-3-R-4: lastCursorId가 null일 경우 가장 최근 것부터 그룹 번개 조회")
    void findByGroupIdAndStatusIn_withNullCursor_shouldReturnFromLatest() {
        // given
        CursorPageable cursorPageable = new CursorPageable(null, 3);

        // when
        CursorPage<BungaeDto> result = bungaeRepository.findByGroupIdAndStatusIn(
                group.getId(),
                null,
                cursorPageable
        );

        // then
        // ID 기준 내림차순 정렬 확인 (최신순)
        List<Long> resultIds = result.getContent().stream()
                                     .map(BungaeDto::id)
                                     .toList();

        List<Long> sortedIds = groupSavedBungaes.stream()
                                                .map(Bungae::getId)
                                                .sorted((a, b) -> Long.compare(b, a)) //  내림차순 정렬
                                                .limit(3)
                                                .toList();

        assertThat(resultIds).isEqualTo(sortedIds);
    }

}
