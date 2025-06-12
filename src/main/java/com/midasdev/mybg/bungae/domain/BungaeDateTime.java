package com.midasdev.mybg.bungae.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BungaeDateTime {

    @Column(name = "bungae_date")
    private LocalDate date;

    @Column(name = "bungae_time")
    private LocalTime time;

    public BungaeDateTime(LocalDate date) {
        this.date = date;
    }

    public BungaeDateTime(LocalTime time) {
        this.time = time;
    }

    // LocalTime 을 파리미터로 받아 dateTime의 시간 정보를 수정
    public void updateTime(LocalTime newTime) {
        this.time = newTime;
    }

}
