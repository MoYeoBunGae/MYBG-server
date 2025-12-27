package com.midasdev.mybg.global.util.cursor_page;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 커서 기반 페이징을 위한 요청 DTO의 추상 클래스입니다. <br>
 * 정렬은 아직 지원하지 않습니다.
 */
// @Schema(description = "커서 기반 페이징 요청 DTO")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class CursorPageRequest {

    @Parameter(
            description =
                    """
            마지막으로 조회된 엔티티의 ID (default: null)
            - null인 경우, 가장 최근 엔티티부터 조회합니다.
            """)
    @PositiveOrZero
    private Long lastCursorId;

    @Parameter(description = "페이지 크기 (default: 10)", example = "10")
    @Positive
    private Integer pageSize = 10;

    public CursorPageable toPageable() {
        return CursorPageable.builder()
                .lastCursorId(lastCursorId)
                .pageSize(pageSize)
                .sortOrder(SortOrder.DESC)
                .build();
    }

    public CursorPageable toPageable(SortOrder sortOrder) {
        return CursorPageable.builder()
                .lastCursorId(lastCursorId)
                .pageSize(pageSize)
                .sortOrder(sortOrder)
                .build();
    }
}
