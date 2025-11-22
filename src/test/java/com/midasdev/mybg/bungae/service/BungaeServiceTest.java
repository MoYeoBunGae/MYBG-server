package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeDateVoteResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeDateVoteRepository;
import com.midasdev.mybg.bungae.repository.BungaeDateVoteTestRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import com.midasdev.mybg.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BungaeServiceTest {

    @Autowired
    private BungaeService bungaeService;

    @Autowired
    private BungaeRepository bungaeRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;

    @Autowired
    private BungaeDateVoteRepository bungaeDateVoteRepository;

    @Autowired
    private BungaeDateVoteTestRepository bungaeDateVoteTestRepository;

    @Autowired
    private BungaeAttendeeRepository bungaeAttendeeRepository;

    private Group group;
    private Member member;
    private Member member2;
    private GroupMember hostGroupMember;
    private GroupMember groupMember2;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.create());
        member2 = memberRepository.save(MemberFixture.createSecondMember());
        group = groupRepository.save(GroupFixture.create(member));
        hostGroupMember = groupMemberRepository.save(GroupMemberFixture.create(group, member));
        groupMember2 = groupMemberRepository.save(GroupMemberFixture.create(group, member2));
    }

    @Test
    @DisplayName("날짜 후보가 1개일 때 Bungae 생성 시 번개 날짜가 fix되어 저장되고, 상태가 RECRUITING이어야 한다")
    void createBungae_withSingleDateCandidate_shouldSetStatusRecruiting() {
        // given
        BungaeCreateRequest request = new BungaeCreateRequest(
                "테스트 번개",
                "설명",
                2,
                10,
                false,
                "서울",
                LocalTime.of(18, 0),
                List.of(LocalDate.now().plusDays(1)),
                null,
                group.getId()
        );

        // when
        Bungae bungae = bungaeService.createBungae(member, request);

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.RECRUITING);
        assertThat(bungae.getBungaeDateTime().getDate()).isEqualTo(request.dateCandidates().get(0));
        assertThat(bungae.getBungaeDateTime().getTime()).isEqualTo(request.bungaeTime());
        assertThat(bungae.getGroup().getId()).isEqualTo(request.groupId());
        assertThat(bungae.getHost().getId()).isEqualTo(hostGroupMember.getId());
    }

    @Test
    @DisplayName("날짜 후보가 2개 이상일 때 Bungae 생성 시 상태가 DATE_VOTING이고, 날짜 정보 없이 시간만 저장된다")
    void createBungae_withMultipleDateCandidates_shouldSetStatusDateVotingAndTimeOnly() {
        // given
        List<LocalDate> dateCandidates = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        LocalTime bungaeTime = LocalTime.of(19, 0);
        LocalDateTime voteClosedAt = LocalDateTime.now().plusDays(1);

        BungaeCreateRequest request = new BungaeCreateRequest(
                "테스트 번개2",
                "설명2",
                2,
                10,
                false,
                "서울",
                bungaeTime,
                dateCandidates,
                voteClosedAt,
                group.getId()
        );

        // when
        Bungae bungae = bungaeService.createBungae(member, request);

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.DATE_VOTING);
        assertThat(bungae.getBungaeDateTime().getDate()).isNull();
        assertThat(bungae.getBungaeDateTime().getTime()).isEqualTo(bungaeTime);
        assertThat(bungae.getGroup().getId()).isEqualTo(request.groupId());
        assertThat(bungae.getHost().getId()).isEqualTo(hostGroupMember.getId());
    }

    @Test
    @DisplayName("날짜 후보가 2개 이상일 때 BungaeRecruitDateOption이 후보 개수만큼 저장된다.")
    void createBungae_withMultipleDateCandidates_shouldSaveAllDateOptionsAndVoteCountIsOne() {
        // given
        List<LocalDate> dateCandidates = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        LocalTime bungaeTime = LocalTime.of(19, 0);
        LocalDateTime voteClosedAt = LocalDateTime.now().plusDays(1);

        BungaeCreateRequest request = new BungaeCreateRequest(
                "테스트 번개2",
                "설명2",
                2,
                10,
                false,
                "서울",
                bungaeTime,
                dateCandidates,
                voteClosedAt,
                group.getId()
        );

        // when
        bungaeService.createBungae(member, request);

        // then
        List<BungaeRecruitDateOption> options = bungaeRecruitDateOptionRepository.findAll();
        assertThat(options).hasSize(dateCandidates.size());
        assertThat(options)
                .extracting(BungaeRecruitDateOption::getDateOption)
                .containsExactlyInAnyOrderElementsOf(dateCandidates);
    }

    @Test
    @DisplayName("B-4-S-1: 올바른 번개 투표 가능 날짜 조회")
    void getBungaeDateVoteOptions_ShouldReturnDateOptions_WhenValidRequest() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember));

        List<LocalDate> dateOptions = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3)
        );

        dateOptions.forEach(dateOption ->
                                    bungaeRecruitDateOptionRepository.save(
                                            BungaeRecruitDateOption.builder()
                                                                   .dateOption(dateOption)
                                                                   .bungae(bungae)
                                                                   .build()
                                    )
        );

        // when
        List<LocalDate> result = bungaeService.getBungaeDateVoteOptions(member, bungae.getId());

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrderElementsOf(dateOptions);
    }

    @Test
    @DisplayName("B-5-S-1: 날짜 투표가 가능한 조건에서 투표가 성공합니다.")
    void B_5_S_1() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember));
        LocalDate voteDate1 = LocalDate.now().plusDays(1);
        LocalDate voteDate2 = LocalDate.now().plusDays(2);
        LocalDate voteDate3 = LocalDate.now().plusDays(3);
        List<LocalDate> voteDates = List.of(voteDate1, voteDate2, voteDate3);
        saveVoteDateOptions(bungae, voteDates);

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(
                member2,
                bungae.getId(),
                voteDates
        );

        // then
        // 해당 번개의 해당 투표자가 투표한 BungaeDateVote 리스트를 조회
        List<BungaeDateVote> votes = bungaeDateVoteTestRepository.findByBungaeIdAndVoterId(
                bungae.getId(),
                groupMember2.getId()
        );

        // 투표한 날짜들 추출
        List<LocalDate> votedDates = votes.stream()
                .map(vote -> vote.getDateOption().getDateOption())
                .toList();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isTrue();
            softly.assertThat(response.failedVoteDates()).isEmpty();
            softly.assertThat(votedDates).containsAll(voteDates);
        });
    }

    /**
     * 번개와 날짜 리스트에 대해 BungaeRecruitDateOption을 저장하는 테스트 유틸 메서드
     */
    private void saveVoteDateOptions(Bungae bungae, List<LocalDate> voteDates) {
        voteDates.forEach(dateOption ->
                bungaeRecruitDateOptionRepository.save(
                        BungaeRecruitDateOption.builder()
                                .dateOption(dateOption)
                                .bungae(bungae)
                                .build()
                )
        );
    }

    @Test
    @DisplayName("B-5-S-2: 같은 날짜에 중복 투표를 할 수 없습니다.")
    void B_5_S_2() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember));
        LocalDate voteDate = LocalDate.now().plusDays(1);
        BungaeRecruitDateOption dateOption = bungaeRecruitDateOptionRepository.save(
                BungaeRecruitDateOption.builder()
                                       .dateOption(voteDate)
                                       .bungae(bungae)
                                       .build()
        );

        // 이미 groupMember2가 voteDate에 투표한 상태로 만듦
        bungaeDateVoteRepository.save(
                BungaeDateVote.builder()
                              .voter(groupMember2)
                              .dateOption(dateOption)
                              .build()
        );

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(member2, bungae.getId(), List.of(voteDate));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isTrue();
            softly.assertThat(response.failedVoteDates()).contains(voteDate);
        });
    }

    @Test
    @DisplayName("B-5-S-3: 최소 인원 도달 시 날짜가 확정되고 해당 날짜의 투표자들은 참가자로 전환됩니다.")
    void B_5_S_3() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember, 2, 10));
        LocalDate voteDate = LocalDate.now().plusDays(1);
        BungaeRecruitDateOption dateOption = bungaeRecruitDateOptionRepository.save(
                BungaeRecruitDateOption.builder()
                                       .dateOption(voteDate)
                                       .bungae(bungae)
                                       .build()
        );
        // 첫 번째 투표자(호스트)가 미리 투표
        bungaeDateVoteRepository.save(
                BungaeDateVote.builder()
                              .voter(hostGroupMember)
                              .dateOption(dateOption)
                              .build()
        );

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(member2, bungae.getId(), List.of(voteDate));

        // then
        // 해당 날짜 투표자들이 모두 BungaeAttendee로 등록되었는지 확인
        List<BungaeDateVote> votes = bungaeDateVoteRepository.findBungaeDateVotesByDateOption(dateOption);
        List<Long> voterIds = votes.stream().map(v -> v.getVoter().getId()).toList();

        List<Long> attendeeIds = bungaeAttendeeRepository.findByBungaeIdAndDeletedFalse(bungae.getId())
                                                         .stream()
                                                         .map(attendee -> attendee.getGroupMember().getId())
                                                         .toList();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isTrue();
            softly.assertThat(response.failedVoteDates()).isEmpty();
            softly.assertThat(response.isDateFixed()).isTrue();
            softly.assertThat(response.fixedDate()).isEqualTo(voteDate);
            softly.assertThat(attendeeIds).containsExactlyInAnyOrderElementsOf(voterIds);
        });
    }

    @Test
    @DisplayName("B-5-S-4: 투표 불가, 참여 가능 시 올바른 반환값을 반환하고, 투표는 처리되지 않습니다.")
    void B_5_S_4() {
        // given
        // 번개가 이미 날짜가 확정되어 RECRUITING 상태(투표 불가, 참여 가능)
        int minAttendees = 2;
        int maxAttendees = 10;
        LocalDate fixedDate = LocalDate.now().plusDays(1);
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithRecruiting(group, hostGroupMember, fixedDate, minAttendees, maxAttendees));

        // minAttendees 이상, maxAttendees 미만의 BungaeAttendee 데이터 세팅
        bungaeAttendeeRepository.saveAll(List.of(
                BungaeAttendee.builder()
                              .bungae(bungae)
                              .groupMember(hostGroupMember)
                              .deleted(false)
                              .build(),
                BungaeAttendee.builder()
                              .bungae(bungae)
                              .groupMember(groupMember2)
                              .deleted(false)
                              .build()
        ));

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(member2, bungae.getId(), List.of(fixedDate));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isFalse();
            softly.assertThat(response.failedVoteDates()).isNull();
            softly.assertThat(response.isJoinable()).isTrue();
            softly.assertThat(response.isDateFixed()).isTrue();
            softly.assertThat(response.fixedDate()).isEqualTo(fixedDate);
            softly.assertThat(response.bungaeStatus()).isEqualTo(BungaeStatus.RECRUITING);
        });

        // TODO: 투표가 처리되지 않았음을 검증
    }

    @Test
    @DisplayName("B-5-S-5: 투표 불가, 참여 불가 시 올바른 반환값을 반환하고, 투표는 처리되지 않습니다.")
    void B_5_S_5() {
        // given
        // 번개가 이미 날짜가 확정되어 RECRUITING_CLOSED 상태(투표 불가, 참여 불가)
        LocalDate fixedDate = LocalDate.now().plusDays(1);
        int minAttendees = 2;
        int maxAttendees = 2;
        Bungae bungae = bungaeRepository.save(
                BungaeFixture.createWithRecruitingClosed(group, hostGroupMember, fixedDate, minAttendees, maxAttendees));

        // maxAttendees와 같은 BungaeAttendee 데이터 세팅
        bungaeAttendeeRepository.saveAll(List.of(
                BungaeAttendee.builder()
                              .bungae(bungae)
                              .groupMember(hostGroupMember)
                              .deleted(false)
                              .build(),
                BungaeAttendee.builder()
                              .bungae(bungae)
                              .groupMember(groupMember2)
                              .deleted(false)
                              .build()
        ));

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(member2, bungae.getId(), List.of(fixedDate));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isFalse();
            softly.assertThat(response.failedVoteDates()).isNull();
            softly.assertThat(response.isJoinable()).isFalse();
            softly.assertThat(response.isDateFixed()).isTrue();
            softly.assertThat(response.fixedDate()).isEqualTo(fixedDate);
            softly.assertThat(response.bungaeStatus()).isEqualTo(BungaeStatus.RECRUITING_CLOSED);
        });

        // TODO: 투표가 처리되지 않았음을 검증
    }

    @Test
    @DisplayName("B-5-S-6: 잘못된 날짜 투표할 수 없습니다.")
    void B_5_S_6() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember));
        // 날짜 후보 세팅
        List<LocalDate> dateOptions = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        dateOptions.forEach(dateOption ->
                bungaeRecruitDateOptionRepository.save(
                        BungaeRecruitDateOption.builder()
                                .dateOption(dateOption)
                                .bungae(bungae)
                                .build()
                )
        );

        // when
        // 존재하지 않는 날짜(후보에 없음)
        LocalDate invalidDate = LocalDate.now().plusDays(100);

        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(member2, bungae.getId(), List.of(invalidDate));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isTrue();
            softly.assertThat(response.failedVoteDates()).contains(invalidDate);
        });
    }

    @Test
    @DisplayName("B-5-S-8: 여러 개의 투표 후보가 동시에 최소 인원을 도달할 수 있는 상황이라면, 가장 빠른 날짜가 번개 날짜로 확정됩니다.")
    void B_5_S_8() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember, 3, 10));

        // 3개의 날짜 후보 생성 (date3이 가장 빠른 날짜)
        LocalDate date1 = LocalDate.now().plusDays(3);
        LocalDate date2 = LocalDate.now().plusDays(2);
        LocalDate date3 = LocalDate.now().plusDays(1);
        List<LocalDate> dates = List.of(date1, date2, date3);

        // 날짜 옵션 생성 및 저장
        List<BungaeRecruitDateOption> options = createAndSaveDateOptions(bungae, dates);

        // 호스트와 groupMember2가 모든 날짜에 투표 (각 날짜마다 2명)
        voteForMultipleDateOptions(hostGroupMember, options);
        voteForMultipleDateOptions(groupMember2, options);

        // when
        // member3가 모든 날짜에 투표하면, 세 날짜 모두 최소 인원(3명)에 도달
        GroupMember groupMember3 = createNewGroupMember("테스트멤버3");

        BungaeDateVoteResponse response = bungaeService.voteBungaeDates(
                groupMember3.getMember(),
                bungae.getId(),
                dates
        );

        // then
        Bungae updatedBungae = bungaeRepository.findById(bungae.getId()).orElseThrow();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.wasVotableBungae()).isTrue();
            softly.assertThat(response.failedVoteDates()).isEmpty();
            softly.assertThat(response.isDateFixed()).isTrue();
            softly.assertThat(response.fixedDate()).isEqualTo(date2);
            softly.assertThat(updatedBungae.getStatus()).isEqualTo(BungaeStatus.RECRUITING);
            softly.assertThat(updatedBungae.getBungaeDate()).isEqualTo(date1);
        });
    }

    /**
     * 번개와 날짜 리스트에 대해 BungaeRecruitDateOption을 저장하고 옵션 리스트를 반환하는 테스트 유틸 메서드
     */
    private List<BungaeRecruitDateOption> createAndSaveDateOptions(Bungae bungae, List<LocalDate> dates) {
        return dates.stream()
                    .map(date -> bungaeRecruitDateOptionRepository.save(
                            BungaeRecruitDateOption.builder()
                                                   .dateOption(date)
                                                   .bungae(bungae)
                                                   .build()
                    ))
                    .toList();
    }

    /**
     * 특정 그룹 멤버가 여러 날짜 옵션에 투표하는 테스트 유틸 메서드
     */
    private void voteForMultipleDateOptions(GroupMember voter, List<BungaeRecruitDateOption> options) {
        List<BungaeDateVote> votes = options.stream()
                                            .map(option -> BungaeDateVote.builder()
                                                                         .voter(voter)
                                                                         .dateOption(option)
                                                                         .build())
                                            .toList();
        bungaeDateVoteRepository.saveAll(votes);
    }

    /**
     * 새로운 멤버와 그룹 멤버를 생성하는 테스트 유틸 메서드
     */
    private GroupMember createNewGroupMember(String memberName) {
        Member newMember = memberRepository.save(MemberFixture.create(memberName));
        return groupMemberRepository.save(GroupMemberFixture.create(group, newMember));
    }
}
