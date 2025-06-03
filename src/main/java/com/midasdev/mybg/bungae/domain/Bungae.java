package com.midasdev.mybg.bungae.domain;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@EntityListeners(AutoCloseable.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bungae {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bungae_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Integer minAttendees;

    @Column(nullable = false)
    private Integer maxAttendees;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column
    private String location;

    @Column
    private BungaeTime bungaeTime;

    @Column(nullable = false)
    private LocalDateTime dateVoteClosedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BungaeStatus status;

    @Embedded
    @Default
    private Audit audit = new Audit();

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMember groupMember;
}
