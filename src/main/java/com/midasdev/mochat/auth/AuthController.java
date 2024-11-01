package com.midasdev.mochat.auth;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.auth.dto.request.AuthRequest;
import com.midasdev.mochat.auth.service.AuthService;
import com.midasdev.mochat.config.security.jwt.AuthorizationToken;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<AuthorizationToken> generateToken(@RequestBody @Valid AuthRequest authRequest) {

        // 1. authtoken 검증 -> idtoken 검증 -> 유저 정보 반환
        TokenRequestUser tokenRequestUser = authService.extractUserInfo(authRequest);

        // 2. 회원가입 여부 (데이터 있는지)
        Optional<Member> memberOptional = memberService.findMemberByOauthAccount(tokenRequestUser);

        // 3. 등록되지 않은 회원이라면 등록하기
        Member member = memberOptional.orElse(memberService.register(tokenRequestUser));

        // 4. accessToken, refreshToken 발급
        AuthorizationToken generatedToken = authService.issueAuthorizationToken(member);

        return ResponseEntity.ok(generatedToken);

    }

}
