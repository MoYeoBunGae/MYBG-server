package com.midasdev.mybg.bungae.controller.dto.response;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
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
        LocalDateTime createdAt // [copilot] 번개가 만들어진 시간 정보 추가
) {

    public static BungaeResponse from(Bungae bungae) {
        return BungaeResponse.builder()
                             .id(bungae.getId())
                             .name(bungae.getName())
                             .description(bungae.getDescription())
                             .minAttendees(bungae.getMinAttendees())
                             .maxAttendees(bungae.getMaxAttendees())
                             .isOnline(bungae.getIsOnline())
                             .location(bungae.getLocation())
                             .bungaeDate(bungae.getBungaeDateTime().getDate())
                             .bungaeTime(bungae.getBungaeDateTime().getTime())
                             .dateVoteClosedAt(bungae.getDateVoteClosedAt())
                             .status(bungae.getStatus())
                             .groupId(bungae.getGroup().getId())
                             .hostGroupMemberId(bungae.getHost().getId())
                             .createdAt(bungae.getAudit().getCreatedAt()) // [copilot] 번개 생성 시간 추가
                             .build();
    }

}
