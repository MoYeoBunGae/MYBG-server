package com.midasdev.mybg.bungae.domain;

import com.midasdev.mybg.group_member.domain.GroupMember;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class BungaeDateVote implements Persistable<BungaeDateVoteId> {

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

    // transient flag to indicate whether this entity is new. Default true for newly built instances.
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    private void markNotNew() {
        this.isNew = false;
    }

}
