package com.midasdev.mybg.bungae.controller.dto.request;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.global.util.cursor_page.CursorPageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetGroupBungaesRequest extends CursorPageRequest {

    @Parameter(
            description = "조회할 그룹 ID",
            required = true,
            example = "1"
    )
    @NotNull
    @Positive
    private Long groupId;

    @Parameter(
            description = """
                    조회할 번개 상태 목록 (default: 모든 상태)
                    - null인 경우, 모든 상태의 번개를 조회합니다.
                    - 여러 상태를 지정할 수 있습니다.
                    """,
            example = "[\"RECRUITING\", \"CLOSED\"]",
            array = @ArraySchema(
                    schema = @Schema(
                            implementation = BungaeStatus.class
                    )
            )
    )
    private List<BungaeStatus> statuses;

}