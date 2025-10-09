package com.midasdev.mybg.bungae.fixture;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BungaeFixture {

    // DATE_VOTING
    public static Bungae createWithDateVoting(Group group, GroupMember host) {
        return bungaeBaseBuilder(group, host)
                .status(BungaeStatus.DATE_VOTING)
                .dateVoteClosedAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    // RECRUITING
    public static Bungae createWithRecruiting(Group group, GroupMember host) {
        return bungaeBaseBuilder(group, host)
                .status(BungaeStatus.RECRUITING)
                .bungaeDateTime(new BungaeDateTime(LocalDate.now().plusDays(2)))
                .build();
    }

    // RECRUITING_CLOSED
    public static Bungae createWithRecruitingClosed(Group group, GroupMember host) {
        return bungaeBaseBuilder(group, host)
                .status(BungaeStatus.RECRUITING_CLOSED)
                .bungaeDateTime(new BungaeDateTime(LocalDate.now().plusDays(2)))
                .build();
    }

    // CLOSED
    public static Bungae createWithClosed(Group group, GroupMember host) {
        return bungaeBaseBuilder(group, host)
                .status(BungaeStatus.CLOSED)
                .bungaeDateTime(new BungaeDateTime(LocalDate.now().minusDays(1)))
                .build();
    }

    // CANCEL
    public static Bungae createWithCancelled(Group group, GroupMember host) {
        return bungaeBaseBuilder(group, host)
                .status(BungaeStatus.CANCELLED)
                .bungaeDateTime(new BungaeDateTime(LocalDate.now().minusDays(1)))
                .build();
    }


    private static Bungae.BungaeBuilder bungaeBaseBuilder(Group group, GroupMember host) {
        return Bungae.builder()
                     .name("테스트 번개")
                     .description("테스트용 번개 설명")
                     .minAttendees(3)
                     .maxAttendees(10)
                     .isOnline(false)
                     .location("서울 강남구")
                     .deleted(false)
                     .group(group)
                     .host(host);
    }

    public static Bungae createWithStatus(Group group, GroupMember host, BungaeStatus status) {
        return Bungae.builder()
                     .name("테스트 번개")
                     .description("테스트용 번개 설명")
                     .minAttendees(2)
                     .maxAttendees(10)
                     .isOnline(false)
                     .location("서울 강남구")
                     .bungaeDateTime(new BungaeDateTime(LocalDate.now().plusDays(1), LocalTime.of(18, 0)))
                     .status(status)
                     .deleted(false)
                     .group(group)
                     .host(host)
                     .build();
    }

    public static Bungae createWithMinAttendees(Group group, GroupMember host, int minAttendees) {
        return Bungae.builder()
                     .name("테스트 번개")
                     .description("테스트용 번개 설명")
                     .minAttendees(minAttendees)
                     .maxAttendees(minAttendees + 10)
                     .isOnline(false)
                     .location("서울 강남구")
                     .bungaeDateTime(new BungaeDateTime(LocalDate.now().plusDays(1), LocalTime.of(18, 0)))
                     .status(BungaeStatus.RECRUITING)
                     .deleted(false)
                     .group(group)
                     .host(host)
                     .build();
    }

    public static Bungae createWithBungaeDateTime(Group group, GroupMember host, BungaeDateTime bungaeDateTime) {
        return Bungae.builder()
                     .name("테스트 번개")
                     .description("테스트용 번개 설명")
                     .minAttendees(2)
                     .maxAttendees(10)
                     .isOnline(false)
                     .location("서울 강남구")
                     .bungaeDateTime(bungaeDateTime)
                     .status(BungaeStatus.RECRUITING)
                     .deleted(false)
                     .group(group)
                     .host(host)
                     .build();
    }

}

