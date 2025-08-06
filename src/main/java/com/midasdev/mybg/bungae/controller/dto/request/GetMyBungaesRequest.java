package com.midasdev.mybg.bungae.controller.dto.request;

import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.global.util.cursor_page.CursorPageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyBungaesRequest extends CursorPageRequest {

    @Parameter(
            name = "statuses",
            in = ParameterIn.QUERY,
            description = """
        조회할 번개 상태값 리스트
        - 가능한 값: RECRUITING, RECRUITING_CLOSED, DATE_VOTING, CLOSED, CANCELLED
        """,
            array = @ArraySchema(
                    schema = @Schema(
                            implementation = BungaeStatus.class
                    )
            )

    )
    private List<BungaeStatus> statuses;

}
