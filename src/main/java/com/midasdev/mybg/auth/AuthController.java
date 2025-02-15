package com.midasdev.mybg.auth;

import com.midasdev.mybg.auth.dto.TokenRequestUser;
import com.midasdev.mybg.auth.dto.request.AuthRequest;
import com.midasdev.mybg.auth.dto.request.TokenReIssueRequest;
import com.midasdev.mybg.auth.dto.response.AuthResponse;
import com.midasdev.mybg.auth.dto.response.TokenReIssueResponse;
import com.midasdev.mybg.auth.service.AuthService;
import com.midasdev.mybg.config.security.jwt.AuthorizationToken;
import com.midasdev.mybg.config.security.jwt.JwtClaimResolver;
import com.midasdev.mybg.config.security.jwt.TokenAttribute;
import com.midasdev.mybg.config.security.jwt.constant.JwtComponent;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 APIs", description = "로그인, 로그아웃 등 인증과 관련된 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final JwtClaimResolver jwtClaimResolver;

    @Operation(summary = "로그인 API", description = "Social 인증 후 서버에서 발급한 authToken을 통해 accessToken, refreshToken을 발급합니다.")
    @PostMapping
    public ResponseEntity<AuthResponse> generateToken(@RequestBody @Valid AuthRequest authRequest) {

        // 1. authtoken 검증 -> idtoken 검증 -> 유저 정보 반환
        TokenRequestUser tokenRequestUser = authService.extractUserInfo(authRequest);

        // 2. 회원가입 여부 (데이터 있는지)
        Optional<Member> memberOptional = memberService.findMemberByOauthAccount(tokenRequestUser);

        // 3. 등록되지 않은 회원이라면 등록하기
        Member member = memberOptional.orElseGet(() -> memberService.register(tokenRequestUser));

        // 4. accessToken, refreshToken 발급
        AuthorizationToken generatedToken = authService.issueAuthorizationToken(member);

        return ResponseEntity.ok(AuthResponse.from(member, generatedToken, memberOptional.isEmpty()));

    }

    @Operation(summary = "access token 재발급 API", description = "refresh token을 통해 새로운 accessToken, refreshToken을 발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenReIssueResponse> reIssueToken(@RequestBody TokenReIssueRequest tokenReIssueRequest) {
        Long memberId = Long.valueOf(
                jwtClaimResolver.extractValueWithoutValidation(tokenReIssueRequest.refreshToken(), TokenAttribute.SUB.getAttribute(),
                                                               JwtComponent.BODY));

        // 1. Body의 refreshToken이 member의 refreshToken과 일치하는지 확인
        authService.verifyRefreshToken(memberId, tokenReIssueRequest.refreshToken());
        // 2. 일치한다면 새로운 accessToken, refreshToken 발급
        AuthorizationToken generatedToken = authService.issueAuthorizationToken(memberId);
        TokenReIssueResponse tokenReIssueResponse = TokenReIssueResponse.builder()
                                                                        .memberId(memberId)
                                                                        .accessToken(generatedToken.getAccessToken())
                                                                        .refreshToken(generatedToken.getRefreshToken())
                                                                        .build();
        return ResponseEntity.ok(tokenReIssueResponse);
    }

    // Refactor: SecurityRequirement name 상수 관리에 대해 생각
    @Operation(summary = "로그아웃 API", description = "사용자의 refresh token을 삭제하여 로그아웃합니다.", security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Member member) {
        authService.logoutMember(member.getId());
        return ResponseEntity.ok(String.format("Member %d is logged out", member.getId()));
    }

}
