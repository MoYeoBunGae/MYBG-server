package com.midasdev.mybg.bungae.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class BungaeRecruitDateOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bungae_recruit_date_option_id")
    private Long id;

    @Column(name = "date_option", nullable = false)
    private LocalDate dateOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bungae_id", nullable = false)
    private Bungae bungae;

}
