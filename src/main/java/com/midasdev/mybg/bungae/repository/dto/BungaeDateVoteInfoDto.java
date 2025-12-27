package com.midasdev.mybg.bungae.repository.dto;

import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.querydsl.core.annotations.QueryProjection;

/**
 * CustomBungaeRecruitDateOptionRepositoryImpl.findVoteInfoByDate() 결과 매핑용 DTO
 *
 * @param dateOption
 * @param voteCount
 * @param voted : 현재 사용자가 해당 날짜에 투표했는지 여부
 */
public record BungaeDateVoteInfoDto(
        BungaeRecruitDateOption dateOption, Integer voteCount, Boolean voted) {

    @QueryProjection
    public BungaeDateVoteInfoDto {}
}
