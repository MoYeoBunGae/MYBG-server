package com.midasdev.mybg.bungae.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BungaeDomainTest {

    private Member member;
    private Group group;
    private GroupMember host;

    @BeforeEach
    void setUp() {
        member = MemberFixture.create();
        group = GroupFixture.create(member);
        host = GroupMemberFixture.create(group, member);
    }

    @Test
    @DisplayName("BD-1-D-1: 번개 상태가 DATE_VOTING 일 때 true 반환")
    void canVote_ShouldReturnTrue_WhenStatusIsDateVoting() {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, BungaeStatus.DATE_VOTING);

        // when
        boolean actualCanVote = bungae.canVote();

        // then
        assertThat(actualCanVote).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BungaeStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DATE_VOTING"})
    @DisplayName("BD-1-D-2: 번개 상태가 DATE_VOTING이 아닐 때 false 반환")
    void canVote_ShouldReturnFalse_WhenStatusIsNotDateVoting(BungaeStatus status) {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, status);

        // when
        boolean actualCanVote = bungae.canVote();

        // then
        assertThat(actualCanVote).isFalse();
    }
}
