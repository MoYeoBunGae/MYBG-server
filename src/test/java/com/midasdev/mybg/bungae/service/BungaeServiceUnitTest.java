package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.fixture.CursorPageableFixture;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class BungaeServiceUnitTest {

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

    @Test
    @DisplayName("B-1-SU-1: 요청 멤버가 그룹에 속하지 않을 때 예외 발생")
    void createBungae_ShouldThrowException_WhenMemberNotInGroup() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);

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

        // when - 그룹은 존재하지만 멤버가 그룹에 속하지 않도록 Mock 설정
        when(groupRepository.findByIdAndDeletedIsFalse(group.getId()))
                .thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMemberAndGroup(member, group))
                .thenReturn(Optional.empty());

        // then - 예외 발생 검증
        assertThatThrownBy(() -> bungaeService.createBungae(member, request))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("그룹이 존재하지 않을 때 예외 발생")
    void findBungaesByGroupIdAndStatuses_ShouldThrowException_WhenGroupNotFound() {
        // given
        Member member = MemberFixture.create();
        List<BungaeStatus> statuses = List.of(BungaeStatus.DATE_VOTING);
        CursorPageable cursorPageable = CursorPageableFixture.create();

        // when - 그룹이 존재하지 않도록 Mock 설정
        when(groupRepository.findByIdAndDeletedIsFalse(any(Long.class)))
                .thenReturn(Optional.empty());

        // then - 예외 발생 검증
        assertThatThrownBy(() -> bungaeService.findBungaesByGroupIdAndStatuses(
                member, 1L, statuses, cursorPageable))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("그룹이 존재하고 멤버가 그룹에 속하지 않을 때 예외 발생")
    void findBungaesByGroupIdAndStatuses_ShouldThrowException_WhenMemberNotInGroup() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);
        List<BungaeStatus> statuses = List.of(BungaeStatus.DATE_VOTING);
        CursorPageable cursorPageable = CursorPageableFixture.create();

        // when - 그룹은 존재하지만 멤버가 그룹에 속하지 않도록 Mock 설정
        when(groupRepository.findByIdAndDeletedIsFalse(any(Long.class)))
                .thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMemberAndGroup(member, group))
                .thenReturn(Optional.empty());

        // then - 예외 발생 검증
        assertThatThrownBy(() -> bungaeService.findBungaesByGroupIdAndStatuses(
                member, 1L, statuses, cursorPageable))
                .isInstanceOf(ApplicationException.class);
    }
}
