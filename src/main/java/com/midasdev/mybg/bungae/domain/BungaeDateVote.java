package com.midasdev.mybg.bungae.domain;

import com.midasdev.mybg.group_member.domain.GroupMember;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class BungaeDateVote {

    @EmbeddedId
    private BungaeDateVoteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voterId")
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMember voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dateOptionId")
    @JoinColumn(name = "bungae_recruit_date_option_id", nullable = false)
    private BungaeRecruitDateOption dateOption;
}
