package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.repository.dto.BungaeDateVoteInfoDto;
import java.time.LocalDate;
import java.util.Optional;

public interface CustomBungaeRecruitDateOptionRepository {
    Optional<BungaeDateVoteInfoDto> findVoteInfoByDate(Long bungaeId, LocalDate dateOption, Long groupMemberId);
}
