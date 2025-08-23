package com.midasdev.mybg.bungae.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BungaeServiceEventTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private BungaeRepository bungaeRepository;
    @Mock
    private BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;
    @Mock
    private BungaeAttendeeRepository bungaeAttendeeRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private BungaeService bungaeService;

    private Group group;
    private Member member;
    private GroupMember hostGroupMember;

    @BeforeEach
    void setUp() {
        member = MemberFixture.create();
        group = GroupFixture.create(member);
        hostGroupMember = GroupMemberFixture.create(group, member);

        when(groupRepository.findByIdAndDeletedIsFalse(group.getId()))
                .thenReturn(Optional.of(group));

        when(groupMemberRepository.findByMemberAndGroup(member, group))
                .thenReturn(Optional.of(hostGroupMember));

        when(bungaeRepository.save(any(Bungae.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("날짜 후보가 1개일 때 번개 생성 시 BungaeVoteCreatedEvent가 발행된다")
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
                group.getId()
        );

        // when
        bungaeService.createBungae(member, request);

        // then
        verify(eventPublisher, times(1))
                .publishEvent(any(BungaeVoteCreatedEvent.class));
    }

    @Test
    @DisplayName("날짜 후보가 2개 이상일 때 번개 생성 시 BungaeVoteCreatedEvent가 발행된다")
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
                group.getId()
        );

        // when
        bungaeService.createBungae(member, request);

        // then
        verify(eventPublisher, times(1))
                .publishEvent(any(BungaeVoteCreatedEvent.class));
    }
}
