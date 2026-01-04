package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import java.util.List;
import java.util.Optional;

public interface CustomBungaeRepository {

    CursorPage<BungaeDto> findAllByAttendeeMemberIdAndStatusIn(
            Long memberId, List<BungaeStatus> statuses, CursorPageable cursorPageable);

    CursorPage<BungaeDto> findByGroupIdAndStatusIn(
            Long groupId, List<BungaeStatus> statuses, CursorPageable pageable, Long memberId);

    Optional<BungaeDto> findBungaeDtoById(Long bungaeId, Long memberId);
}
