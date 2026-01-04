package com.midasdev.mybg.bungae.controller.dto.response;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.global.util.cursor_page.LongIdentifiable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record BungaeResponse(
        Long id,
        String name,
        String description,
        Integer minAttendees,
        Integer maxAttendees,
        Integer attendeeCount,
        Boolean isOnline,
        String location,
        LocalDate bungaeDate,
        LocalTime bungaeTime,
        LocalDateTime dateVoteClosedAt,
        BungaeStatus status,
        Long groupId,
        Long hostGroupMemberId,
        LocalDateTime createdAt,
        Boolean hasJoined,
        Boolean hasVoted)
        implements LongIdentifiable {

    public static BungaeResponse from(Bungae bungae) {
        return BungaeResponse.builder()
                .id(bungae.getId())
                .name(bungae.getName())
                .description(bungae.getDescription())
                .minAttendees(bungae.getMinAttendees())
                .maxAttendees(bungae.getMaxAttendees())
                .isOnline(bungae.getIsOnline())
                .location(bungae.getLocation())
                .bungaeDate(bungae.getBungaeDate())
                .bungaeTime(bungae.getBungaeTime())
                .dateVoteClosedAt(bungae.getDateVoteClosedAt())
                .status(bungae.getStatus())
                .groupId(bungae.getGroup().getId())
                .hostGroupMemberId(bungae.getHost().getId())
                .createdAt(bungae.getCreatedAt())
                .hasJoined(null)
                .hasVoted(null)
                .build();
    }

    public static BungaeResponse from(BungaeDto bungaeDto) {
        return BungaeResponse.builder()
                .id(bungaeDto.id())
                .name(bungaeDto.name())
                .description(bungaeDto.description())
                .minAttendees(bungaeDto.minAttendees())
                .maxAttendees(bungaeDto.maxAttendees())
                .attendeeCount(bungaeDto.attendeeCount())
                .isOnline(bungaeDto.isOnline())
                .location(bungaeDto.location())
                .bungaeDate(bungaeDto.bungaeDate())
                .bungaeTime(bungaeDto.bungaeTime())
                .dateVoteClosedAt(bungaeDto.dateVoteClosedAt())
                .status(bungaeDto.status())
                .groupId(bungaeDto.groupId())
                .hostGroupMemberId(bungaeDto.hostGroupMemberId())
                .createdAt(bungaeDto.createdAt())
                .hasJoined(bungaeDto.hasJoined())
                .hasVoted(bungaeDto.hasVoted())
                .build();
    }

    @Override
    public Long getId() {
        return id;
    }
}
