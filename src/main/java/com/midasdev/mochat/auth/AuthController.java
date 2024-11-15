package com.midasdev.mochat.auth;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.auth.dto.request.AuthRequest;
import com.midasdev.mochat.auth.dto.request.TokenReIssueRequest;
import com.midasdev.mochat.auth.dto.response.AuthResponse;
import com.midasdev.mochat.auth.dto.response.TokenReIssueResponse;
import com.midasdev.mochat.auth.service.AuthService;
import com.midasdev.mochat.config.security.jwt.AuthorizationToken;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.TokenAttribute;
import com.midasdev.mochat.config.security.jwt.constant.JwtComponent;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.member.service.MemberService;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final JwtClaimResolver jwtClaimResolver;

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

    @PostMapping("/reissue")
    public ResponseEntity<TokenReIssueResponse> reIssueToken(@RequestBody TokenReIssueRequest tokenReIssueRequest) {
        Long memberId = Long.valueOf(
                jwtClaimResolver.extractValueWithoutValidation(tokenReIssueRequest.refreshToken(), TokenAttribute.SUB.getAttribute(),
                                                               JwtComponent.BODY));

        // 1. Body의 refreshToken이 member의 refreshToken과 일치하는지 확인
        memberService.verifyRefreshToken(memberId, tokenReIssueRequest.refreshToken());
        // 2. 일치한다면 새로운 accessToken, refreshToken 발급
        AuthorizationToken generatedToken = authService.issueAuthorizationToken(memberId);
        TokenReIssueResponse tokenReIssueResponse = TokenReIssueResponse.builder()
                                                                        .memberId(memberId)
                                                                        .accessToken(generatedToken.getAccessToken())
                                                                        .refreshToken(generatedToken.getRefreshToken())
                                                                        .build();
        return ResponseEntity.ok(tokenReIssueResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Member member) {
        authService.logoutMember(member.getId());
        return ResponseEntity.ok(String.format("Member %d is logged out", member.getId()));
    }

}
