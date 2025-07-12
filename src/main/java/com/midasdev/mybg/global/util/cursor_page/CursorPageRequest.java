package com.midasdev.mybg.global.util.cursor_page;

import com.midasdev.mybg.global.util.default_value_mapper.Default;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커서 기반 페이징을 위한 요청 DTO의 추상 클래스입니다. <br>
 * 정렬은 아직 지원하지 않습니다.
 */
@Schema(description = "커서 기반 페이징 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class CursorPageRequest {

    @Schema(
            description = "커서(마지막으로 조회된 엔티티의 ID) (default: 0)",
            example = "100",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @Default("0")
    private Long lastCursorId;

    @Schema(
            description = "페이지 크기 (default: 10)",
            example = "10",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @Default("10")
    private Integer pageSize;

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
