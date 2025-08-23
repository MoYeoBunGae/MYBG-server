package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
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
import org.junit.jupiter.api.AfterEach;
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

    private Group group;
    private Member member;
    private GroupMember hostGroupMember;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.create());
        group = groupRepository.save(GroupFixture.create(member));
        hostGroupMember = groupMemberRepository.save(GroupMemberFixture.create(group, member));
    }

    @AfterEach
    void clear() {
        bungaeRecruitDateOptionRepository.deleteAll();
        bungaeRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        memberRepository.deleteAll();
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
}
