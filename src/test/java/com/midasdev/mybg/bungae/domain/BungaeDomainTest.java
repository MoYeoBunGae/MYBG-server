package com.midasdev.mybg.bungae.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BungaeDomainTest {

    @ParameterizedTest
    @MethodSource("provideStatusAndExpectedCanVote")
    @DisplayName("BD-1-D-1: 번개 상태에 따른 canVote 결과 검증")
    void canVote_ShouldReturnExpectedResult_BasedOnBungaeStatus(BungaeStatus status, boolean expectedCanVote) {
        // given
        Member member = MemberFixture.create();
        Group group = GroupFixture.create(member);
        GroupMember host = GroupMemberFixture.create(group, member);
        Bungae bungae = BungaeFixture.createWithStatus(group, host, status);

        // when
        boolean actualCanVote = bungae.canVote();

        // then
        assertThat(actualCanVote).isEqualTo(expectedCanVote);
    }

    private static Stream<Arguments> provideStatusAndExpectedCanVote() {
        return Stream.of(
                Arguments.of(BungaeStatus.DATE_VOTING, true),
                Arguments.of(BungaeStatus.RECRUITING, false),
                Arguments.of(BungaeStatus.RECRUITING_CLOSED, false),
                Arguments.of(BungaeStatus.CANCELLED, false),
                Arguments.of(BungaeStatus.CLOSED, false)
        );
    }
}

