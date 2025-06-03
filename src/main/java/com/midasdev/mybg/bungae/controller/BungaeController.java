package com.midasdev.mybg.bungae.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
            @AuthenticationPrincipal Member loginMember,
            @Valid @RequestBody BungaeCreateRequest request
    ) {
        Bungae bungae = bungaeService.createBungae(loginMember, request);
        return ResponseEntity.ok(BungaeResponse.from(bungae));
    }



}
