package com.midasdev.mochat.config.security.handler;


import com.midasdev.mochat.config.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${client.url}")
    private String clientUrl;

    @Value("${client.redirect.auth-success}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("Authentication Success! Redirect with authToken...");

        // 1. idToken을 포함한 Auth token 생성
        // 2. client 에게 Redirect로 auth token 전송

        // 해당 핸들러는 Oauth 인증 핸들러이므로 형변환 진행
        // TODO: 타입 검사 로직
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        OAuth2User principal = token.getPrincipal();
        String authToken = jwtProvider.createAuthToken(principal);
        String provider = token.getAuthorizedClientRegistrationId().toUpperCase();
        String clientRedirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl)
                                                       .path(redirectUrl)
                                                       .queryParam("token", authToken)
                                                       .queryParam("provider", provider)
                                                       .toUriString();
        getRedirectStrategy().sendRedirect(request, response, clientRedirectUrl);
    }

}
