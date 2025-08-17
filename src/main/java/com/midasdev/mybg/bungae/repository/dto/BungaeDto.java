package com.midasdev.mybg.bungae.repository.dto;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.global.util.cursor_page.LongIdentifiable;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BungaeDto(
        Long id,
        String name,
        String description,
        Integer minAttendees,
        Integer maxAttendees,
        Boolean isOnline,
        String location,
        LocalDate bungaeDate,
        LocalTime bungaeTime,
        LocalDateTime dateVoteClosedAt,
        BungaeStatus status,
        LocalDateTime createdAt,
        Boolean deleted,
        Long groupId,
        Long hostGroupMemberId,
        Integer attendeeCount
) implements LongIdentifiable {

    @QueryProjection
    public BungaeDto {
    }

    @Override
    public Long getId() {
        return id;
    }

}
