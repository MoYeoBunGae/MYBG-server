package com.midasdev.mybg.group.domain;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "\"group\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_invitation_code",
                        columnNames = { "invitation_code" })
        })
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private String invitationCode;

    @Column(nullable = false)
    private int maxMemberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_member_id")
    private Member owner;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean deleted;

    @Embedded
    @Default
    private Audit audit = new Audit();

    @OneToOne(mappedBy = "group")
    GroupStatistics groupStatistics;

    public void updateInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public boolean isOwnedBy(Member member) {
        return this.owner.getId().equals(member.getId());
    }

    public int getTotalMemberCount() {
        return this.groupStatistics.getTotalMemberCount();
    }

    public boolean isFull() {
        return this.getTotalMemberCount() >= this.maxMemberCount;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateMaxMemberCount(int newMaxCount) {
        int currentTotal = this.getTotalMemberCount();
        if (newMaxCount < currentTotal) {
            throw new ApplicationException(
                    ApplicationExceptionType.GROUP_MAX_COUNT_BELOW_CURRENT,
                    newMaxCount, currentTotal
            );
        }
        this.maxMemberCount = newMaxCount;
    }

    public void updateProfileImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

}
