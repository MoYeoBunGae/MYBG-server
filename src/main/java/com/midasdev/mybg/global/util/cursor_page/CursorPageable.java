package com.midasdev.mybg.global.util.cursor_page;

import lombok.Builder;

/**
 * 커서 기반 페이징을 위한 DTO입니다. <br>
 * 단일 정렬 방향만 지원합니다. (보통 시간 기준 정렬)
 * 추후 필요할 경우, 정렬 컬럼을 포함할 수 있는 방향으로 확장합니다.
 * @param lastCursorId
 * @param pageSize
 * @param sortOrder
 */
@Builder
public record CursorPageable(
        Long lastCursorId,
        Integer pageSize,
        SortOrder sortOrder
) {
    public CursorPageable(Long lastCursorId, Integer pageSize) {
        this(lastCursorId, pageSize, SortOrder.ASC);
    }
}
