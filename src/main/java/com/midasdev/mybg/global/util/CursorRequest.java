package com.midasdev.mybg.global.util;

import com.midasdev.mybg.global.util.default_value_mapper.Default;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "커서 기반 페이징 요청 DTO")
public record CursorRequest(
        @Schema(
                description = "커서(마지막으로 조회된 엔티티의 ID) (default: 0)",
                example = "100",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        @Default("0")
        Long cursorId,

        @Schema(
                description = "페이지 크기 (default: 10)",
                example = "10",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        @Default("10")
        Integer pageSize
) {

}
