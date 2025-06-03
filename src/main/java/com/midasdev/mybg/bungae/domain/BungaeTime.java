package com.midasdev.mybg.bungae.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BungaeTime {

    @Column(name = "bungae_datetime")
    private LocalDateTime dateTime;

    @Transient
    private boolean isTimeConfirmed;

    // LocalTime 을 파리미터로 받아 dateTime의 시간 정보를 수정
    public void updateTime(LocalTime newDateTime) {
        this.dateTime = dateTime.withHour(newDateTime.getHour())
                                .withMinute(newDateTime.getMinute())
                                .withSecond(newDateTime.getSecond());
        this.isTimeConfirmed = true;
    }

}
