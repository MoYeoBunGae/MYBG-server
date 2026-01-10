package com.midasdev.mybg.bungae.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.request.BungaeDateVoteRequest;
import com.midasdev.mybg.bungae.controller.dto.request.GetGroupBungaesRequest;
import com.midasdev.mybg.bungae.controller.dto.request.GetMyBungaesRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeDateVoteOptionResponse;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeDateVoteResponse;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.service.BungaeService;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Bungae APIs")
@RestController
@RequestMapping("/api/v1/bungaes")
@RequiredArgsConstructor
@Validated
public class BungaeController {

    private final BungaeService bungaeService;

    @Operation(
            summary = "[BUNGAE-001] 번개 생성 API",
            description =
                    """
                    번개를 생성합니다.
                    - Request DTO : BungaeCreateRequest
                    - Response DTO : BungaeResponse
                    - 세부사항:
                        1. 날짜 후보가 한 개라면 상태는 RECRUITING으로 설정됩니다.
                        2. 날짜 후보가 여러 개라면 상태는 DATE_VOTING으로 설정됩니다.
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @PostMapping()
    public ResponseEntity<BungaeResponse> createBungae(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody BungaeCreateRequest request) {
        Bungae bungae = bungaeService.createBungae(member, request);
        return ResponseEntity.ok(BungaeResponse.from(bungae));
    }

    @Operation(
            summary = "[BUNGAE-002] 내 번개모임 목록 조회 API",
            description =
                    """
                    로그인한 사용자가 참여한 번개모임 목록을 조회합니다.
                    커서 페이지네이션을 지원합니다.
                    - Request DTO : GetMyBungaesRequest
                    - Response DTO : CursorPage<BungaeResponse>
                    - 세부사항:
                        1. 가장 마지막 번개가 포함되어 있을 경우, nextCursorId가 null이고 hasNext가 false로 반환됩니다.
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping(value = "/me")
    public ResponseEntity<CursorPage<BungaeResponse>> getMyBungaes(
            @AuthenticationPrincipal Member member, @Valid GetMyBungaesRequest request) {
        CursorPage<BungaeDto> bungaes =
                bungaeService.findBungaesByMemberIdAndStatuses(
                        member, request.getStatuses(), request.toPageable());
        CursorPage<BungaeResponse> responses = bungaes.map(BungaeResponse::from);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "[BUNGAE-005] 그룹별 번개모임 목록 조회 API",
            description =
                    """
                    특정 그룹의 번개모임 목록을 조회합니다.
                    커서 페이지네이션을 지원합니다.
                    - Request DTO : GetGroupBungaesRequest
                    - Response DTO : CursorPage<BungaeResponse>
                    - 세부사항:
                        1. groupId는 필수 파라미터입니다.
                        2. statuses가 null인 경우, 모든 상태의 번개를 조회합니다.
                        3. 가장 마지막 번개가 포함되어 있을 경우, nextCursorId가 null이고 hasNext가 false로 반환됩니다.
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping(value = "/group")
    public ResponseEntity<CursorPage<BungaeResponse>> getGroupBungaes(
            @AuthenticationPrincipal Member member, @Valid GetGroupBungaesRequest request) {
        CursorPage<BungaeDto> bungaes =
                bungaeService.findBungaesByGroupIdAndStatuses(
                        member, request.getGroupId(), request.getStatuses(), request.toPageable());
        CursorPage<BungaeResponse> responses = bungaes.map(BungaeResponse::from);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "[BUNGAE-003] 번개 투표 가능한 날짜들 조회 API",
            description =
                    """
                    번개의 투표 가능한 날짜들을 조회합니다.
                    - Request Parameter : bungaeId (Path Variable)
                    - Response DTO : BungaeDateVoteOptionResponse
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping(value = "/{bungaeId}/date-vote-options")
    public ResponseEntity<BungaeDateVoteOptionResponse> getBungaeDateVoteOptions(
            @AuthenticationPrincipal Member member, @PathVariable Long bungaeId) {
        List<LocalDate> dateOptions = bungaeService.getBungaeDateVoteOptions(member, bungaeId);
        return ResponseEntity.ok(new BungaeDateVoteOptionResponse(dateOptions));
    }

    @Operation(
            summary = "[BUNGAE-009] 번개 날짜 투표 API",
            description =
                    """
                    번개 날짜에 투표합니다.
                    - Response : BungaeDateVoteResponse (전체적인 투표 결과)
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @PostMapping(value = "/{bungaeId}/date-vote")
    public ResponseEntity<BungaeDateVoteResponse> voteBungaeDate(
            @AuthenticationPrincipal Member member,
            @PathVariable Long bungaeId,
            @Valid @RequestBody BungaeDateVoteRequest request) {
        BungaeDateVoteResponse response =
                bungaeService.voteBungaeDates(member, bungaeId, request.getVoteDates());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "[BUNGAE-010] 번개 참여 API",
            description =
                    """
                    번개에 참여합니다.
                    - Response DTO : BungaeResponse (updated with current attendeeCount and status)
                    - 세부사항:
                        1. 번개 상태가 RECRUITING일 때만 참여 가능합니다.
                        2. 번개가 속한 그룹의 멤버만 참여 가능합니다.
                        3. 이미 참여한 번개에는 중복 참여가 불가능합니다.
                        4. 참여로 인해 최대 인원에 도달하면 자동으로 RECRUITING_CLOSED로 상태가 변경됩니다.
                    """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @PostMapping("/{bungaeId}/join")
    public ResponseEntity<BungaeResponse> joinBungae(
            @AuthenticationPrincipal Member member, @PathVariable Long bungaeId) {
        BungaeDto bungaeDto = bungaeService.joinBungae(member, bungaeId);
        return ResponseEntity.ok(BungaeResponse.from(bungaeDto));
    }
}
