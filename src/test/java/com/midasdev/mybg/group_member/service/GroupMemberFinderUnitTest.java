package com.midasdev.mybg.group_member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.midasdev.mybg.TestConstant;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupMemberFinderUnitTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupMemberFinder groupMemberFinder;

    private Member member;
    private Group group;

    @BeforeEach
    void setUp() {
        member = MemberFixture.create();
        group = GroupFixture.create(member);
    }

    @Test
    @DisplayName("GMF-1-SU-1: 존재하는 멤버와 그룹으로 조회 시 GroupMember 반환")
    void findByMemberAndGroup_ShouldReturnGroupMember_WhenMemberBelongsToGroup() {
        // given
        GroupMember expectedGroupMember = GroupMemberFixture.create(group, member);

        given(groupMemberRepository.findByMemberAndGroup(member, group))
                .willReturn(Optional.of(expectedGroupMember));

        // when
        GroupMember actualGroupMember = groupMemberFinder.findByMemberAndGroup(member, group);

        // then
        assertThat(actualGroupMember).isEqualTo(expectedGroupMember);
    }

    @Test
    @DisplayName("GMF-1-SU-2: 멤버가 그룹에 속하지 않을 때 예외 발생")
    void findByMemberAndGroup_ShouldThrowException_WhenMemberNotBelongsToGroup() {
        // given
        given(groupMemberRepository.findByMemberAndGroup(member, group))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupMemberFinder.findByMemberAndGroup(member, group))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID);
    }
}

