package com.midasdev.mybg.bungae.domain;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.cursor_page.LongIdentifiable;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bungae implements LongIdentifiable {

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

    @Embedded
    private BungaeDateTime bungaeDateTime;

    @Column
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
    @JoinColumn(name = "host_group_member_id", nullable = false)
    private GroupMember host;

    public LocalDate getBungaeDate() {
        return bungaeDateTime != null ? bungaeDateTime.getDate() : null;
    }

    public LocalTime getBungaeTime() {
        return bungaeDateTime != null ? bungaeDateTime.getTime() : null;
    }

    public LocalDateTime getCreatedAt() {
        return audit != null ? audit.getCreatedAt() : null;
    }

    public LocalDateTime getUpdatedAt() {
        return audit != null ? audit.getModifiedAt() : null;
    }

    public boolean canVote() {
        return this.status == BungaeStatus.DATE_VOTING;
    }

    // 최소 인원까지 한자리가 남았는지 여부
    public boolean isOneLeftToMinAttendees(int currentAttendeeCount) {
        return currentAttendeeCount == (this.minAttendees - 1);
    }

    public void confirmDate(LocalDate date) {
        if (this.status != BungaeStatus.DATE_VOTING) {
            throw new ApplicationException(ApplicationExceptionType.INVALID_BUNGAE_STATUS_FOR_DATE_CONFIRMATION, this.id, this.status);
        }


        // 확정된 날짜로 번개 날짜 업데이트
        if (this.bungaeDateTime == null) {
            this.bungaeDateTime = new BungaeDateTime(date, null);
        } else {
            this.bungaeDateTime.updateDate(date);
        }

        // 상태 변경
        if (Objects.equals(minAttendees, maxAttendees)) {
            this.status = BungaeStatus.RECRUITING_CLOSED;
        } else if (minAttendees < maxAttendees) {
            this.status = BungaeStatus.RECRUITING;
        } else {
            throw new ApplicationException(ApplicationExceptionType.INVALID_ATTENDEE_LIMITS, this.id, this.minAttendees, this.maxAttendees);
        }
    }

    public boolean isDateFixed() {
        return this.bungaeDateTime != null && this.bungaeDateTime.isDateSet();
    }

    public boolean canJoin() {
        return this.status == BungaeStatus.RECRUITING;
    }

}
