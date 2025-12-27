package com.midasdev.mybg.bungae.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void updateDate(LocalDate date) {
        this.date = date;
    }

    public boolean isDateSet() {
        return this.date != null;
    }
}
