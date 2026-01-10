package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midasdev.mybg.TestConstant;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.fixture.CursorPageableFixture;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.lock.NamedLockManager;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group.service.GroupFinder;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.group_member.service.GroupMemberFinder;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class BungaeServiceUnitTest {

    @Mock private BungaeRepository bungaeRepository;

    @Mock private BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;

    @Mock private BungaeAttendeeRepository bungaeAttendeeRepository;

    @Mock private ApplicationEventPublisher eventPublisher;

    @Mock private BungaeFinder bungaeFinder;

    @Mock private GroupMemberFinder groupMemberFinder;

    @Mock private GroupFinder groupFinder;

    @Mock private NamedLockManager namedLockManager;

    @InjectMocks private BungaeService bungaeService;

    @Test
    @DisplayName("B-2-SU-1: BungaeStatus가 DATE_VOTING인 번개는 내가 참여한 번개로 조회할 수 없습니다.")
    void B_2_SU_1() {
        // given
        Member member = MemberFixture.create();
        List<BungaeStatus> statuses = List.of(BungaeStatus.RECRUITING, BungaeStatus.DATE_VOTING);
        CursorPageable cursorPageable = CursorPageableFixture.create();

        // when & then
        assertThatThrownBy(
                        () ->
                                bungaeService.findBungaesByMemberIdAndStatuses(
                                        member, statuses, cursorPageable))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(
                        TestConstant.EXCEPTION_TYPE_FIELD,
                        ApplicationExceptionType.INVALID_BUNGAE_STATUS);
    }

    @Test
    @DisplayName("B-3-SU-1: 멤버가 그룹에 속하는지 검증 기능 호출")
    void B_3_SU_1() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.createWithId(groupOwner);
        List<BungaeStatus> statuses = List.of(BungaeStatus.DATE_VOTING);
        CursorPageable cursorPageable = CursorPageableFixture.create();

        // when
        when(groupFinder.findById(any(Long.class))).thenReturn(group);
        bungaeService.findBungaesByGroupIdAndStatuses(
                member, group.getId(), statuses, cursorPageable);

        // then
        verify(groupMemberFinder).findByMemberAndGroup(member, group);
    }

    @Test
    @DisplayName("B-4-SU-1: 번개의 상태가 DATE_VOTING이 아닐 때 예외 발생")
    void B_4_SU_1() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);
        GroupMember groupMember = GroupMemberFixture.create(group, member);
        Bungae bungae = BungaeFixture.createWithRecruiting(group, groupMember);
        Long bungaeId = 1L;

        when(bungaeFinder.findById(bungaeId)).thenReturn(bungae);

        // when & then
        assertThatThrownBy(() -> bungaeService.getBungaeDateVoteOptions(member, bungaeId))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(
                        TestConstant.EXCEPTION_TYPE_FIELD,
                        ApplicationExceptionType.BUNGAE_VOTE_UNAVAILABLE);
    }

    @ParameterizedTest
    @EnumSource(
            value = BungaeStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"RECRUITING"})
    @DisplayName("B-6-SU-1: 모집중인 상태가 아닌 번개에 대해 참여할 수 없다")
    void B_6_SU_1(BungaeStatus status) {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);
        GroupMember groupMember = GroupMemberFixture.create(group, member);
        Bungae bungae = BungaeFixture.createWithStatus(group, groupMember, status);
        Long bungaeId = 1L;

        when(bungaeFinder.findById(bungaeId)).thenReturn(bungae);
        when(namedLockManager.tryAcquire(any(String.class), any(Integer.class))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> bungaeService.joinBungae(member, bungaeId))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(
                        TestConstant.EXCEPTION_TYPE_FIELD,
                        ApplicationExceptionType.BUNGAE_NOT_JOINABLE_STATUS);
    }

    @Test
    @DisplayName("B-6-SU-2: 그룹 멤버가 아닌 사용자는 번개에 참여할 수 없다")
    void B_6_SU_2() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);
        GroupMember host = GroupMemberFixture.create(group, groupOwner);
        Bungae bungae = BungaeFixture.createWithRecruiting(group, host);
        Long bungaeId = 1L;

        when(bungaeFinder.findById(bungaeId)).thenReturn(bungae);
        when(namedLockManager.tryAcquire(any(String.class), any(Integer.class))).thenReturn(true);
        when(groupMemberFinder.findByMemberAndGroup(member, group))
                .thenThrow(
                        new ApplicationException(
                                ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                                member.getId(),
                                group.getId()));

        // when & then
        assertThatThrownBy(() -> bungaeService.joinBungae(member, bungaeId))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(
                        TestConstant.EXCEPTION_TYPE_FIELD,
                        ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID);
    }

    @Test
    @DisplayName("B-6-SU-3: 이미 번개에 참여한 멤버는 다시 참여할 수 없다")
    void B_6_SU_3() {
        // given
        Member member = MemberFixture.create();
        Member groupOwner = MemberFixture.create();
        Group group = GroupFixture.create(groupOwner);
        GroupMember host = GroupMemberFixture.create(group, groupOwner);
        GroupMember groupMember = GroupMemberFixture.create(group, member);
        Bungae bungae = BungaeFixture.createWithRecruiting(group, host);
        BungaeAttendee existingAttendee =
                BungaeAttendee.builder().bungae(bungae).groupMember(groupMember).build();
        Long bungaeId = 1L;

        when(bungaeFinder.findById(bungaeId)).thenReturn(bungae);
        when(namedLockManager.tryAcquire(any(String.class), any(Integer.class))).thenReturn(true);
        when(groupMemberFinder.findByMemberAndGroup(member, group)).thenReturn(groupMember);
        when(bungaeAttendeeRepository.findByBungaeAndGroupMemberAndDeletedFalse(
                        bungae, groupMember))
                .thenReturn(Optional.of(existingAttendee));

        // when & then
        assertThatThrownBy(() -> bungaeService.joinBungae(member, bungaeId))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(
                        TestConstant.EXCEPTION_TYPE_FIELD,
                        ApplicationExceptionType.BUNGAE_ALREADY_JOINED);
    }
}
