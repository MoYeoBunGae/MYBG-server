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
                     .minAttendees(2)
                     .maxAttendees(10)
                     .isOnline(false)
                     .location("서울 강남구")
                     .deleted(false)
                     .group(group)
                     .host(host);
    }
}

