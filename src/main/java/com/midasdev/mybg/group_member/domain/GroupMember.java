package com.midasdev.mybg.group_member.domain;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "group_member",
        uniqueConstraints = @UniqueConstraint(
                name = "uq__group_member__member_id__group_id",
                columnNames = { "member_id", "group_id" }
        ))
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "member_profile_image_url", nullable = false)
    private String memberProfileImageUrl;

    @Embedded
    @Default
    private Audit audit = new Audit();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deleted;

    public boolean belongsTo(Long memberId) {
        if (this.member == null) {
            throw new ApplicationException(ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR,
                                           "GroupMember is not initialized. memberId: " + memberId);
        }
        return this.member.getId().equals(memberId);
    }

    public void updateNickname(String nickname) {
        if (nickname.isBlank()) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_NICKNAME_NOT_BLANK);
        }

        this.nickname = nickname;
    }

    public void updateProfileImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApplicationException(ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR,
                                           "GroupMember image URL is not valid. imageUrl: " + imageUrl);
        }

        this.memberProfileImageUrl = imageUrl;
    }

    public void leave() {
        if (this.deleted) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_ALREADY_LEFT);
        }
        this.deleted = true;
        this.leftAt = LocalDateTime.now();
    }

}
