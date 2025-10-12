package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import com.midasdev.mybg.bungae.domain.BungaeDateVoteId;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeDateVoteRepository;
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
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BungaeServiceConcurrencyTest {

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

    @AfterEach
    void clear() {
        bungaeDateVoteRepository.deleteAll();
        bungaeRecruitDateOptionRepository.deleteAll();
        bungaeAttendeeRepository.deleteAll();
        bungaeRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("B-5-S-7: 최소모집인원까지 1명의 투표가 남은 상황에서 100명이 동시에 투표를 할 경우 한 명만 성공합니다.")
    void voteBungaeDate_ShouldAllowOnlyOneVote_WhenHundredVoteSimultaneouslyAtMinAttendeesMinusOne() throws Exception {
        // given
        Bungae bungae = bungaeRepository.save(BungaeFixture.createWithDateVoting(group, hostGroupMember, 2, 5));
        LocalDate voteDate = LocalDate.now().plusDays(1);
        BungaeRecruitDateOption dateOption = bungaeRecruitDateOptionRepository.save(
                BungaeRecruitDateOption.builder()
                        .dateOption(voteDate)
                        .bungae(bungae)
                        .build()
        );
        // 이미 한 명이 투표한 상태로 만듦 (minAttendees=2, 현재 1명 투표)
        bungaeDateVoteRepository.save(
                BungaeDateVote.builder()
                        .id(new BungaeDateVoteId(hostGroupMember.getId(), dateOption.getId()))
                        .voter(hostGroupMember)
                        .dateOption(dateOption)
                        .build()
        );

        // 100명의 멤버/그룹멤버 생성
        Member[] members = new Member[100];
        for (int i = 0; i < 100; i++) {
            members[i] = memberRepository.save(MemberFixture.create(String.valueOf(i)));
            groupMemberRepository.save(GroupMemberFixture.create(group, members[i]));
        }

        // when: 100명이 동시에 투표 시도
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                bungaeService.voteBungaeDate(members[idx], bungae.getId(), voteDate);
            });
        }
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();

        // then: 투표 성공자는 1명, 실패자는 99명이어야 함
        long voteCount = bungaeDateVoteRepository.findBungaeDateVotesByDateOption(dateOption).size();
        assertThat(voteCount).isEqualTo(2);

        // 참여자로 등록된 인원도 2명이어야 함
        List<BungaeAttendee> attendees = bungaeAttendeeRepository.findByBungaeIdAndDeletedFalse(bungae.getId());

        assertThat(attendees).hasSize(2);
    }
}
