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
 *     * group : groupMember (member가 group에 참여자로 등록된 상태)
 *     * otherGroup : otherGroupMember(otherMember), memberInOtherGroup(member)
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
    private List<Bungae> otherGroupSavedBungaes;


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
        otherGroupSavedBungaes = new ArrayList<>();

        // DATE_VOTING 번개 생성
        Bungae dateVotingBungae = bungaeRepository.save(
                BungaeFixture.createWithDateVoting(group, groupMember));
        groupSavedBungaes.add(dateVotingBungae);
        // DATE_VOTING 번개 생성 (다른 그룹 멤버)
        otherGroupSavedBungaes.add(bungaeRepository.save(BungaeFixture.createWithDateVoting(otherGroup, otherGroupMember)));

        // RECRUITING 번개 생성
        Bungae recruitingBungae = bungaeRepository.save(
                BungaeFixture.createWithRecruiting(group, groupMember));
        groupSavedBungaes.add(recruitingBungae);
        // RECRUITING 번개 생성 (다른 그룹 멤버)
        Bungae otherRecruitingBungae = bungaeRepository.save(BungaeFixture.createWithRecruiting(otherGroup, otherGroupMember));
        otherGroupSavedBungaes.add(otherRecruitingBungae);

        // RECRUITING_CLOSED 번개 생성
        Bungae recruitingClosedBungae = bungaeRepository.save(
                BungaeFixture.createWithRecruitingClosed(group, groupMember));
        groupSavedBungaes.add(recruitingClosedBungae);
        // RECRUITING_CLOSED 번개 생성 (다른 그룹 멤버)
        otherGroupSavedBungaes.add(bungaeRepository.save(BungaeFixture.createWithRecruitingClosed(otherGroup, otherGroupMember)));

        // CLOSED 번개 생성
        Bungae closedBungae = bungaeRepository.save(
                BungaeFixture.createWithClosed(group, groupMember));
        groupSavedBungaes.add(closedBungae);
        // CLOSED 번개 생성 (다른 그룹 멤버)
        Bungae otherClosedBungae = bungaeRepository.save(BungaeFixture.createWithClosed(otherGroup, otherGroupMember));
        otherGroupSavedBungaes.add(otherClosedBungae);

        // CANCELLED 번개 생성
        Bungae cancelledBungae = bungaeRepository.save(
                BungaeFixture.createWithCancelled(group, groupMember));
        groupSavedBungaes.add(cancelledBungae);
        // CANCELLED 번개 생성 (다른 그룹 멤버)
        otherGroupSavedBungaes.add(bungaeRepository.save(BungaeFixture.createWithCancelled(otherGroup, otherGroupMember)));

        // 각 번개에 대해 BungaeAttendee 생성 (member가 참여자로 등록)
        saveAttendee(groupSavedBungaes, groupMember);
        saveAttendee(otherGroupSavedBungaes, otherGroupMember);

        //  member를 otherGroup의 RECRUITING, CLOSED 번개에 참여자로 추가
        saveAttendeeForMemberInOtherGroup(otherRecruitingBungae, memberInOtherGroup);
        saveAttendeeForMemberInOtherGroup(otherClosedBungae, memberInOtherGroup);
    }

    private void saveAttendee(List<Bungae> bungaes, GroupMember groupMember) {
        for (Bungae bungae : bungaes) {
            BungaeAttendee attendee = BungaeAttendee.builder()
                                                    .bungae(bungae)
                                                    .groupMember(groupMember)
                                                    .deleted(false)
                                                    .build();
            bungaeAttendeeRepository.save(attendee);
        }
    }

    private void saveAttendeeForMemberInOtherGroup(Bungae bungae, GroupMember groupMember) {
        BungaeAttendee attendee = BungaeAttendee.builder()
                                                .bungae(bungae)
                                                .groupMember(groupMember)
                                                .deleted(false)
                                                .build();
        bungaeAttendeeRepository.save(attendee);
    }

    @Test
    @DisplayName("lastCursorId, size 조건에 맞는 번개만 조회된다")
    void findAllByAttendeeMemberIdAndStatusIn_withCursorAndSize_shouldReturnCorrectBungaes() {
        // given
        Long lastCursorId = Long.MAX_VALUE;
        int pageSize = 2;
        CursorPageable cursorPageable = new CursorPageable(lastCursorId, pageSize);

        // when
        CursorPage<Bungae> result = bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(
                member.getId(),
                null,
                cursorPageable
        );

        // then
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getContent())
                .extracting(Bungae::getId)
                .allMatch(id -> id < lastCursorId); // cursor보다 작은 ID만 조회되어야 함
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
        assertThat(result.getContent()).hasSize(3); // DATE_VOTING 1개 + RECRUITING 2개
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
                null,
                cursorPageable
        );

        // then
        // member가 참여한 번개: group의 5개 + otherGroup의 2개 (RECRUITING, CLOSED)
        assertThat(result.getContent()).hasSize(7);
        assertThat(result.getContent())
                .extracting(Bungae::getStatus)
                .contains(
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
        CursorPageable cursorPageable = new CursorPageable(null, 3);

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

        //  member가 참여한 모든 번개 중 최신 3개를 가져와서 검증
        List<Long> allMemberBungaeIds = new ArrayList<>();
        allMemberBungaeIds.addAll(groupSavedBungaes.stream().map(Bungae::getId).toList());
        //  otherGroup의 번개 중 member가 참여하고 있는 RECRUITING, CLOSED 번개 ID 추가
        allMemberBungaeIds.addAll(otherGroupSavedBungaes.stream()
                                                        .filter(bungae -> bungae.getStatus() == BungaeStatus.RECRUITING
                                                                || bungae.getStatus() == BungaeStatus.CLOSED)
                                                        .map(Bungae::getId).toList());

        List<Long> sortedIds = allMemberBungaeIds.stream()
                                                 .sorted((a, b) -> Long.compare(b, a))
                                                 .limit(3)
                                                 .toList();

        assertThat(resultIds).isEqualTo(sortedIds);
        assertThat(result.getNextCursorId()).isEqualTo(result.getContent().get(2).getId());
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
