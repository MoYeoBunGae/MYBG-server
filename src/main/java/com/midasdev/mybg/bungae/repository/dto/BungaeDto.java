package com.midasdev.mybg.bungae.repository.dto;

import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record BungaeDto(
        Long id,
        String name,
        String description,
        Integer minAttendees,
        Integer maxAttendees,
        Boolean isOnline,
        String location,
        BungaeDateTime bungaeDateTime,
        LocalDateTime dateVoteClosedAt,
        BungaeStatus status,
        LocalDateTime createdAt,
        Boolean deleted,
        Long groupId,
        Long hostGroupMemberId,
        Integer attendeeCount
) {

    @QueryProjection
    public BungaeDto {
    }

}
