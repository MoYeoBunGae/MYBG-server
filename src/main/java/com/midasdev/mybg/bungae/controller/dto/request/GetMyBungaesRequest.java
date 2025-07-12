package com.midasdev.mybg.bungae.controller.dto.request;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.global.util.cursor_page.CursorPageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "내 번개모임 목록 조회 요청 DTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyBungaesRequest extends CursorPageRequest {

    @Schema(
            title = "번개 상태",
            description = "조회할 번개 상태값 리스트",
            example = "[\"RECRUITING\", \"DATE_VOTING\"]",
            allowableValues = {"RECRUITING", "RECRUITING_CLOSED", "DATE_VOTING", "CLOSED", "CANCELED"},
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    private List<BungaeStatus> statuses;

}
