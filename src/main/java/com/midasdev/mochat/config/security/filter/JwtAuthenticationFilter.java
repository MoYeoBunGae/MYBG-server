package com.midasdev.mochat.config.security.filter;

import com.midasdev.mochat.config.security.jwt.GrantType;
import com.midasdev.mochat.config.security.jwt.JwtClaimResolver;
import com.midasdev.mochat.config.security.jwt.TokenAttribute;
import com.midasdev.mochat.config.security.jwt.TokenType;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.midasdev.mochat.global.util.assertion.Assertion;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtClaimResolver jwtClaimResolver;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        getTokenFromHeader(request).ifPresent((token) -> {
            // 1. Bearer 토큰 검증
            String accessToken = validateBearerToken(token);

            // 2. AccessToken 에서 memberId 가져오기
            Long memberId = Long.valueOf(
                    jwtClaimResolver.extractValue(accessToken, TokenType.ACCESS, TokenAttribute.SUB.getAttribute())
            );

            // 3. memberId에 대한 인증 정보 저장
            setAuthenticationInSecurityContext(memberId);
        });

        doFilter(request, response, filterChain);
    }

    private void setAuthenticationInSecurityContext(Long memberId) {
        // 1. memberId 있는지 검사
        Member member = memberService.findMemberById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID, memberId));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member, null, new ArrayList<>());

        // 2. memberId에 대한 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String validateBearerToken(String token) {
        Assertion.with(token)
                 .setValidation((t) -> t.startsWith(GrantType.BEARER.getType()))
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.NOT_BEARER_TOKEN));
        return token.substring(GrantType.BEARER.getType().length()).strip();
    }

    public Optional<String> getTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
    }

}
