package com.midasdev.mybg.group.domain;

import com.midasdev.mybg.global.audit.Audit;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupStatistics {

    @Id
    @Column(name = "group_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private Integer totalMemberCount;

    @Column(nullable = false)
    private Integer totalBungaeCount;

    @Embedded
    @Default
    private Audit audit = new Audit();

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean deleted;

    public void increaseTotalMemberCount() {
        this.totalMemberCount++;
    }

    public void decreaseTotalMemberCount() {
        if (this.totalMemberCount <= 1) {
            throw new ApplicationException(ApplicationExceptionType.GLOBAL_INTERNAL_SERVER_ERROR,
                                           "그룹 인원 수가 0이하로 감소할 수 없습니다.");
        }
        this.totalMemberCount--;
    }

}
