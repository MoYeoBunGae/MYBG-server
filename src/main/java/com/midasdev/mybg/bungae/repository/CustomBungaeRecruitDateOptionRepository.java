package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.repository.dto.BungaeDateVoteInfoDto;
import java.time.LocalDate;
import java.util.List;

public interface CustomBungaeRecruitDateOptionRepository {

    List<BungaeDateVoteInfoDto> findVoteInfoByDates(
            Long bungaeId, List<LocalDate> dateOptions, Long groupMemberId);
}
