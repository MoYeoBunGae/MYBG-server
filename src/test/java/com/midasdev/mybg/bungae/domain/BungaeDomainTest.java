package com.midasdev.mybg.bungae.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.midasdev.mybg.TestConstant;
import java.time.LocalDate;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName("BD-1-D-1: canVote - 번개 상태가 DATE_VOTING 일 때 true 반환")
    void isVotableStatus_ShouldReturnTrue_WhenStatusIsDateVoting() {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, BungaeStatus.DATE_VOTING);

        // when
        boolean actualCanVote = bungae.isVotableStatus();

        // then
        assertThat(actualCanVote).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BungaeStatus.class, mode = EnumSource.Mode.EXCLUDE, names = { "DATE_VOTING" })
    @DisplayName("BD-1-D-2: canVote - 번개 상태가 DATE_VOTING이 아닐 때 false 반환 - ParameterizedTest")
    void isVotableStatus_ShouldReturnFalse_WhenStatusIsNotDateVoting(BungaeStatus status) {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, status);

        // when
        boolean actualCanVote = bungae.isVotableStatus();

        // then
        assertThat(actualCanVote).isFalse();
    }

    @Test
    @DisplayName("BD-2-D-1: isOneLeftToMinAttendees - 현재 인원수가 (최소 인원 - 1)일 때 true 반환")
    void isOneLeftToMinAttendees_ShouldReturnTrue_WhenCurrentIsMinMinusOne() {
        // given
        int minAttendees = 5;
        Bungae bungae = BungaeFixture.createWithMinAttendees(group, host, minAttendees);

        int currentAttendeeCount = minAttendees - 1;

        // when
        boolean result = bungae.isOneLeftToMinAttendees(currentAttendeeCount);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @DisplayName("BD-2-D-2: isOneLeftToMinAttendees - 현재 인원수가 (최소 인원 - 1)이 아닐 때 false 반환 - ParameterizedTest")
    @ValueSource(ints = { 0, 2, 6, 10 })
    void isOneLeftToMinAttendees_ShouldReturnFalse_WhenCurrentIsNotMinMinusOne(int currentAttendeeCount) {
        // given
        int minAttendees = 5;
        Bungae bungae = BungaeFixture.createWithMinAttendees(group, host, minAttendees);

        // when
        boolean result = bungae.isOneLeftToMinAttendees(currentAttendeeCount);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = BungaeStatus.class, mode = EnumSource.Mode.EXCLUDE, names = { "DATE_VOTING" })
    @DisplayName("BD-3-D-1: confirmDate - 날짜 확정시 번개 상태가 DATE_VOTING이 아닐 때 예외 발생")
    void confirmDate_ShouldThrowException_WhenStatusIsNotDateVoting(BungaeStatus status) {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, status);

        // when & then
        assertThatThrownBy(() -> bungae.confirmDate(LocalDate.now()))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.INVALID_BUNGAE_STATUS_FOR_DATE_CONFIRMATION);
    }

    @Test
    @DisplayName("BD-3-D-2: confirmDate - 번개 상태가 DATE_VOTING이고 min==max일 때 상태가 RECRUITING_CLOSED로 변경")
    void confirmDate_ShouldSetStatusRecruitingClosed_WhenMinEqualsMax() {
        // given
        int min = 5;
        int max = 5;
        Bungae bungae = Bungae.builder()
                              .name("테스트 번개")
                              .description("테스트용 번개 설명")
                              .minAttendees(min)
                              .maxAttendees(max)
                              .status(BungaeStatus.DATE_VOTING)
                              .host(host)
                              .group(group)
                              .build();

        // when
        bungae.confirmDate(LocalDate.of(2024, 7, 1));

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.RECRUITING_CLOSED);
    }

    @Test
    @DisplayName("BD-3-D-3: confirmDate - 번개 상태가 DATE_VOTING이고 min<max일 때 상태가 RECRUITING으로 변경")
    void confirmDate_ShouldSetStatusRecruiting_WhenMinLessThanMax() {
        // given
        int min = 3;
        int max = 5;
        Bungae bungae = Bungae.builder()
                              .name("테스트 번개")
                              .description("테스트용 번개 설명")
                              .minAttendees(min)
                              .maxAttendees(max)
                              .host(host)
                              .group(group)
                              .status(BungaeStatus.DATE_VOTING)
                              .build();

        // when
        bungae.confirmDate(LocalDate.of(2024, 7, 2));

        // then
        assertThat(bungae.getStatus()).isEqualTo(BungaeStatus.RECRUITING);
    }

    @Test
    @DisplayName("BD-3-D-4: confirmDate - 번개 상태가 DATE_VOTING이고 min>max일 때 예외 발생")
    void confirmDate_ShouldThrowException_WhenMinGreaterThanMax() {
        // given
        int min = 6;
        int max = 5;
        Bungae bungae = Bungae.builder()
                              .name("테스트 번개")
                              .description("테스트용 번개 설명")
                              .minAttendees(min)
                              .maxAttendees(max)
                              .host(host)
                              .group(group)
                              .status(BungaeStatus.DATE_VOTING)
                              .build();

        // when & then
        assertThatThrownBy(() -> bungae.confirmDate(LocalDate.of(2024, 7, 3)))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.INVALID_ATTENDEE_LIMITS);
    }

    @Test
    @DisplayName("BD-3-D-5: confirmDate - 번개 날짜가 null일 때 bungaeDateTime이 새로 생성되어 날짜가 설정됨")
    void confirmDate_ShouldCreateBungaeDateTime_WhenNull() {
        // given
        Bungae bungae = Bungae.builder().
                              name("테스트 번개")
                              .description("테스트용 번개 설명")
                              .minAttendees(2)
                              .maxAttendees(10)
                              .status(BungaeStatus.DATE_VOTING)
                              .host(host)
                              .group(group)
                              .build();

        // when
        bungae.confirmDate(LocalDate.of(2024, 7, 4));

        // then
        assertThat(bungae.getBungaeDate()).isEqualTo(LocalDate.of(2024, 7, 4));
    }

    @Test
    @DisplayName("BD-3-D-6: confirmDate - 번개 시간 정보가 이미 있을 때 날짜가 업데이트 됨")
    void confirmDate_ShouldUpdateDate_WhenBungaeDateTimeExists() {
        // given
        Bungae bungae = Bungae.builder().
                              name("테스트 번개")
                              .description("테스트용 번개 설명")
                              .minAttendees(2)
                              .maxAttendees(10)
                              .status(BungaeStatus.DATE_VOTING)
                              .host(host)
                              .group(group)
                              .bungaeDateTime(new BungaeDateTime(LocalTime.of(18, 0)))
                              .build();
        // when
        bungae.confirmDate(LocalDate.of(2024, 7, 5));

        // then
        assertThat(bungae.getBungaeDate()).isEqualTo(LocalDate.of(2024, 7, 5));
    }

    @Test
    @DisplayName("BD-4-D-1: isDateFixed - bungaeDateTime에 날짜가 설정되어 있으면 true 반환")
    void isDateFixed_ShouldReturnTrue_WhenDateIsSet() {
        // given
        Bungae bungae = BungaeFixture.createWithBungaeDateTime(group, host,
                new BungaeDateTime(LocalDate.of(2024, 7, 6), LocalTime.of(18, 0)));
        // when
        boolean result = bungae.isDateFixed();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("BD-4-D-2: isDateFixed - bungaeDateTime에 날짜가 설정되어 있지 않으면 false 반환")
    void isDateFixed_ShouldReturnFalse_WhenDateIsNotSet() {
        // given
        Bungae bungae = BungaeFixture.createWithBungaeDateTime(group, host,
                new BungaeDateTime(LocalTime.of(18, 0)));

        // when
        boolean result = bungae.isDateFixed();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("BD-4-D-3: isDateFixed - bungaeDateTime이 null일 경우 false 반환")
    void isDateFixed_ShouldReturnFalse_WhenBungaeDateTimeIsNull() {
        // given
        Bungae bungae = BungaeFixture.createWithBungaeDateTime(group, host, null);

        // when
        boolean result = bungae.isDateFixed();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("BD-5-D-1: canJoin - 번개 상태가 RECRUITING일 때 true 반환")
    void canJoin_ShouldReturnTrue_WhenStatusIsRecruiting() {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, BungaeStatus.RECRUITING);

        // when
        boolean result = bungae.canJoin();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = BungaeStatus.class, mode = EnumSource.Mode.EXCLUDE, names = { "RECRUITING" })
    @DisplayName("BD-5-D-2: canJoin - 번개 상태가 RECRUITING이 아닐 때 false 반환 - ParameterizedTest")
    void canJoin_ShouldReturnFalse_WhenStatusIsNotRecruiting(BungaeStatus status) {
        // given
        Bungae bungae = BungaeFixture.createWithStatus(group, host, status);

        // when
        boolean result = bungae.canJoin();

        // then
        assertThat(result).isFalse();
    }

}
