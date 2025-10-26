package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.midasdev.mybg.TestConstant;
import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeDateVoteResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import com.midasdev.mybg.bungae.domain.BungaeDateVoteId;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeDateVoteRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
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
    void voteBungaeDate_ShouldSucceedAndIncreaseVoteCount_WhenVoteIsAvailable() {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember));
        LocalDate voteDate = LocalDate.now().plusDays(1);
        BungaeRecruitDateOption dateOption = bungaeRecruitDateOptionRepository.save(
                BungaeRecruitDateOption.builder()
                                       .dateOption(voteDate)
                                       .bungae(bungae)
                                       .build()
        );

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDate(member2, bungae.getId(), voteDate);

        // then
        assertThat(response.isVoteSucceeded()).isTrue();

        // BungaeDateVote 데이터가 실제로 추가되었는지 검증
        List<BungaeDateVote> votes = bungaeDateVoteRepository.findBungaeDateVotesByDateOption(dateOption);
        assertThat(votes.stream()
                        .anyMatch(voter -> voter.getVoter().getId().equals(groupMember2.getId())))
                .isTrue();
    }

    @Test
    @DisplayName("B-5-S-2: 같은 날짜에 중복 투표를 할 수 없습니다.")
    void voteBungaeDate_ShouldThrowException_WhenAlreadyVotedForSameDate() {
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
                              .id(new BungaeDateVoteId(groupMember2.getId(), dateOption.getId()))
                              .voter(groupMember2)
                              .dateOption(dateOption)
                              .build()
        );

        // when & then
        assertThatThrownBy(() -> bungaeService.voteBungaeDate(member2, bungae.getId(), voteDate))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.ALREADY_VOTED_FOR_BUNGAE_DATE);
    }

    @Test
    @DisplayName("B-5-S-3: 최소 인원 도달 시 날짜가 확정되고 해당 날짜의 투표자들은 참가자로 전환됩니다.")
    void voteBungaeDate_ShouldFixDateAndConvertVotersToAttendees_WhenMinAttendeesReached() {
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
                              .id(new BungaeDateVoteId(hostGroupMember.getId(), dateOption.getId()))
                              .voter(hostGroupMember)
                              .dateOption(dateOption)
                              .build()
        );

        // when
        BungaeDateVoteResponse response = bungaeService.voteBungaeDate(member2, bungae.getId(), voteDate);

        // then
        assertThat(response.isDateFixed()).isTrue();
        assertThat(response.fixedDate()).isEqualTo(voteDate);

        // 해당 날짜 투표자들이 모두 BungaeAttendee로 등록되었는지 확인
        List<BungaeDateVote> votes = bungaeDateVoteRepository.findBungaeDateVotesByDateOption(dateOption);
        List<Long> voterIds = votes.stream().map(v -> v.getVoter().getId()).toList();

        List<Long> attendeeIds = bungaeAttendeeRepository.findByBungaeIdAndDeletedFalse(bungae.getId())
                                                         .stream()
                                                         .map(attendee -> attendee.getGroupMember().getId())
                                                         .toList();

        assertThat(attendeeIds).containsExactlyInAnyOrderElementsOf(voterIds);
    }

    @Test
    @DisplayName("B-5-S-4: 투표 불가, 참여 가능 시 올바른 반환값을 반환합니다.")
    void voteBungaeDate_ShouldReturnJoinableTrueAndDateFixed_WhenVoteUnavailableButJoinable() {
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
        BungaeDateVoteResponse response = bungaeService.voteBungaeDate(member2, bungae.getId(), fixedDate);

        // then
        assertThat(response.isVoteSucceeded()).isFalse();
        assertThat(response.isJoinable()).isTrue();
        assertThat(response.isDateFixed()).isTrue();
        assertThat(response.fixedDate()).isEqualTo(fixedDate);
        assertThat(response.bungaeStatus()).isEqualTo(BungaeStatus.RECRUITING);
    }

    @Test
    @DisplayName("B-5-S-5: 투표 불가, 참여 불가 시 올바른 반환값을 반환합니다.")
    void voteBungaeDate_ShouldReturnJoinableFalseAndDateFixed_WhenVoteUnavailableAndNotJoinable() {
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
        BungaeDateVoteResponse response = bungaeService.voteBungaeDate(member2, bungae.getId(), fixedDate);

        // then
        assertThat(response.isVoteSucceeded()).isFalse();
        assertThat(response.isJoinable()).isFalse();
        assertThat(response.isDateFixed()).isTrue();
        assertThat(response.fixedDate()).isEqualTo(fixedDate);
        assertThat(response.bungaeStatus()).isEqualTo(BungaeStatus.RECRUITING_CLOSED);
    }

    @Test
    @DisplayName("B-5-S-6: 잘못된 날짜 투표할 수 없습니다.")
    void voteBungaeDate_ShouldThrowException_WhenVoteDateOptionNotFound() {
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

        // when & then
        // 존재하지 않는 날짜(후보에 없음)
        LocalDate invalidDate = LocalDate.now().plusDays(100);

        assertThatThrownBy(() -> bungaeService.voteBungaeDate(member2, bungae.getId(), invalidDate))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.BUNGAE_DATE_OPTION_NOT_FOUND);
    }
}
