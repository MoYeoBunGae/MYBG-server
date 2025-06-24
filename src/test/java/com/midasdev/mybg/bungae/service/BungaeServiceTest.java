package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.bungae.service.event.BungaeVoteCreatedEvent;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
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

    @SpyBean
    private ApplicationEventPublisher eventPublisher;

    private Group group;
    private GroupMember hostGroupMember;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(MemberFixture.create());
        group = groupRepository.save(GroupFixture.create(member));
        hostGroupMember = groupMemberRepository.save(GroupMemberFixture.create(group, member));
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
                group.getId(),
                hostGroupMember.getId()
        );

        // when
        Bungae bungae = bungaeService.createBungae(request);

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.RECRUITING);
        assertThat(bungae.getBungaeDateTime().getDate()).isEqualTo(request.dateCandidates().get(0));
        assertThat(bungae.getBungaeDateTime().getTime()).isEqualTo(request.bungaeTime());
        assertThat(bungae.getGroup().getId()).isEqualTo(request.groupId());
        assertThat(bungae.getHost().getId()).isEqualTo(request.hostGroupMemberId());
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
                group.getId(),
                hostGroupMember.getId()
        );

        // when
        Bungae bungae = bungaeService.createBungae(request);

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.DATE_VOTING);
        assertThat(bungae.getBungaeDateTime().getDate()).isNull();
        assertThat(bungae.getBungaeDateTime().getTime()).isEqualTo(bungaeTime);
        assertThat(bungae.getGroup().getId()).isEqualTo(request.groupId());
        assertThat(bungae.getHost().getId()).isEqualTo(request.hostGroupMemberId());
    }

    @Test
    @DisplayName("날짜 후보가 2개 이상일 때 BungaeRecruitDateOption이 후보 개수만큼 저장되고, 각 투표수는 1이다")
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
                group.getId(),
                hostGroupMember.getId()
        );

        // when
        bungaeService.createBungae(request);

        // then
        List<BungaeRecruitDateOption> options = bungaeRecruitDateOptionRepository.findAll();
        assertThat(options).hasSize(dateCandidates.size());
        assertThat(options)
                .extracting(BungaeRecruitDateOption::getDateOption)
                .containsExactlyInAnyOrderElementsOf(dateCandidates);
        assertThat(options)
                .extracting(BungaeRecruitDateOption::getVoteCount)
                .containsOnly(1);
    }

    @Test
    @DisplayName("날짜 후보가 1개일 때 번개 생성 시 이벤트가 발행된다")
    void createBungae_withSingleDateCandidate_shouldPublishEvent() {
        // given
        BungaeCreateRequest request = new BungaeCreateRequest(
                "이벤트 테스트 번개1",
                "설명",
                2,
                10,
                false,
                "서울",
                LocalTime.of(18, 0),
                List.of(LocalDate.now().plusDays(1)),
                null,
                group.getId(),
                hostGroupMember.getId()
        );

        // when
        bungaeService.createBungae(request);

        // then
        Mockito.verify(eventPublisher, Mockito.times(1))
               .publishEvent(Mockito.argThat(event -> event instanceof BungaeVoteCreatedEvent));
    }

    @Test
    @DisplayName("날짜 후보가 2개 이상일 때 번개 생성 시 이벤트가 발행된다")
    void createBungae_withMultipleDateCandidates_shouldPublishEvent() {
        // given
        List<LocalDate> dateCandidates = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        LocalTime bungaeTime = LocalTime.of(19, 0);
        LocalDateTime voteClosedAt = LocalDateTime.now().plusDays(1);

        BungaeCreateRequest request = new BungaeCreateRequest(
                "이벤트 테스트 번개2",
                "설명2",
                2,
                10,
                false,
                "서울",
                bungaeTime,
                dateCandidates,
                voteClosedAt,
                group.getId(),
                hostGroupMember.getId()
        );

        // when
        bungaeService.createBungae(request);

        // then
        Mockito.verify(eventPublisher, Mockito.times(1))
                .publishEvent(Mockito.argThat(event -> event instanceof BungaeVoteCreatedEvent));
    }
}
