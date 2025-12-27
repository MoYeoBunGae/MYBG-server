package com.midasdev.mybg.bungae.domain;

import com.midasdev.mybg.group_member.domain.GroupMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_bungae_date_vote_voter_date_option",
                        columnNames = {"group_member_id", "bungae_recruit_date_option_id"}))
public class BungaeDateVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bungae_date_vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMember voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bungae_recruit_date_option_id", nullable = false)
    private BungaeRecruitDateOption dateOption;
}
