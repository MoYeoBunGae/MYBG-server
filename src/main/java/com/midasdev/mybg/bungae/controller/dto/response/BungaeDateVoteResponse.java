package com.midasdev.mybg.bungae.controller.dto.response;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import lombok.Builder;

@Builder
public record BungaeDateVoteResponse(
    boolean isVoteSucceeded,
    boolean isDateFixed,
    boolean isJoinable,
    BungaeStatus bungaeStatus
    // TODO: 채팅방 구현이 정해지면, 들어가는 채팅방을 식별할 수 있는 정보를 추가로 보내야 합니다.
) {}
