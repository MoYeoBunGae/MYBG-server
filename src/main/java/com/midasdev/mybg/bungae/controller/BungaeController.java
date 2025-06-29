package com.midasdev.mybg.bungae.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.request.GetMyBungaesRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.service.BungaeService;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bungae APIs")
@RestController
@RequestMapping("/api/v1/bungaes")
@RequiredArgsConstructor
@Validated
public class BungaeController {

    private final BungaeService bungaeService;

    @Operation(
            summary = "번개 생성 API",
            description = """
                    번개를 생성합니다.
                    - Request DTO : BungaeCreateRequest
                    - Response DTO : BungaeResponse
                    - 세부사항:
                        1. 날짜 후보가 한 개라면 상태는 RECRUITING으로 설정됩니다.
                        2. 날짜 후보가 여러 개라면 상태는 DATE_VOTING으로 설정됩니다.
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    @PostMapping
    public ResponseEntity<BungaeResponse> createBungae(
            @Valid @RequestBody BungaeCreateRequest request
    ) {
        Bungae bungae = bungaeService.createBungae(request);
        return ResponseEntity.ok(BungaeResponse.from(bungae));
    }

    @Operation(
            summary = "내 번개모임 목록 조회 API",
            description = """
                    로그인한 사용자가 참여한 번개모임 목록을 조회합니다.
                    - Request DTO : GetMyBungaesRequest
                    - Response DTO : List<BungaeResponse>
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    @GetMapping("/me")
    public ResponseEntity<List<BungaeResponse>> getMyBungaes(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody GetMyBungaesRequest request
    ) {
        List<Bungae> bungaes = bungaeService.findBungaesByMemberIdAndStatuses(
                member, request.statuses()
        );
        List<BungaeResponse> responses = bungaes.stream()
                                                .map(BungaeResponse::from)
                                                .toList();
        return ResponseEntity.ok(responses);
    }


}
