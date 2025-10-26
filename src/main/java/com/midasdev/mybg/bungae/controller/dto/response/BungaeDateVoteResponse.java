package com.midasdev.mybg.bungae.controller.dto.response;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

// TODO: Swagger 문서 작성
@Builder
public record BungaeDateVoteResponse(
        // 투표 데이터를 반영하기 전에 투표가 가능했는지 여부
        boolean wasVotable,

        // 번개 날짜가 확정되었는지 여부
        boolean isDateFixed,

        // 확정된 날짜에 추가로 참여 가능한지 여부
        boolean isJoinable,

        // 확정된 날짜
        LocalDate fixedDate,

        // 번개 상태
        BungaeStatus bungaeStatus,

        // 투표 실패한 날짜 목록 - wasVotable이 true일 때만 의미 있음
        List<LocalDate> failedVoteDates
        // TODO: 채팅방 구현이 정해지면, 들어가는 채팅방을 식별할 수 있는 정보를 추가로 보내야 합니다.
) {

}
