package com.midasdev.mybg.bungae.controller.dto.response;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "번개 날짜 투표 응답")
public record BungaeDateVoteResponse(
        @Schema(
          description = "투표 요청 시점에 투표가 가능한 상태였는지 여부. <br/>" +
                  "true: 투표 가능 상태(DATE_VOTING)였으며 투표가 처리됨. failedVoteDates로 성공/실패 여부 확인 가능. <br/>" +
                  "false: 이미 날짜가 확정된 상태(RECRUITING, RECRUITING_CLOSED 등)로 투표가 처리되지 않음. <br/>" +
                  "이 경우 isJoinable, fixedDate, bungaeStatus로 현재 번개 상태 파악 가능.",
          example = "true"
        )
        boolean wasVotableBungae,

        @Schema(
                description = "번개 날짜가 확정되었는지 여부. ",
                example = "true"
        )
        boolean isDateFixed,

        @Schema(
description = "확정된 날짜에 추가로 참여 가능한지 여부. <br/>" +
                        "true: 참여 가능(RECRUITING 상태, 현재 참가자 수 &lt; 최대 인원). <br/>" +
                        "false: 참여 불가(RECRUITING_CLOSED 상태, 현재 참가자 수 = 최대 인원). <br/>" +
                        "null: 날짜가 아직 확정되지 않은 경우.",
                example = "true",
                nullable = true
        )
        Boolean isJoinable,

        @Schema(
                description = "확정된 번개 날짜. 확정되지 않은 경우 null.",
                example = "2025-12-01",
                nullable = true
        )
        LocalDate fixedDate,

        @Schema(
                description = "번개의 현재 상태. ",
                example = "DATE_VOTING"
        )
        BungaeStatus bungaeStatus,

        @Schema(
description = "투표가 가능한 번개에 대해 투표에 실패한 날짜 목록.<br/>" +
              "실패 사유: 중복 투표, 존재하지 않는 날짜 후보 등.<br/>" +
              "빈 리스트: 모든 날짜 투표 성공.<br/>" +
              "null: wasVotableBungae가 false인 경우 (투표 불가능 번개).",
                example = "[\"2025-12-05\", \"2025-12-10\"]",
                nullable = true
        )
        List<LocalDate> failedVoteDates
        // TODO: 채팅방 구현이 정해지면, 들어가는 채팅방을 식별할 수 있는 정보를 추가로 보내야 합니다.
) {

}
