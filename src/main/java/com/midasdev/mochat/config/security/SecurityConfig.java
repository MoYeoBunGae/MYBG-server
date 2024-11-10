package com.midasdev.mochat.config.security;

import com.midasdev.mochat.config.security.filter.ExceptionHandlerFilter;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationTextPublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public final AuthenticationSuccessHandler authenticationSuccessHandler;
    public final AuthenticationEntryPoint authenticationEntryPoint;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Value("${client.url}")
    private String clientUrl;

    @Value("${security.permitted-urls}")
    private String[] permitUrlPatterns;

    @Bean
    public SecurityFilterChain oidcLoginFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("SecurityFilterChain -> OidcLoginFilterChain");
        return httpSecurity
                .exceptionHandling(config ->
                                           config.authenticationEntryPoint(authenticationEntryPoint))
//                .securityMatcher("/api/**", "")
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers(permitUrlPatterns)
                                       .permitAll()
                                       .anyRequest().authenticated())
                .csrf(CsrfConfigurer::disable)
                .cors(config -> config.configurationSource(corsConfigurationSource()))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .baseUri("/security/oauth2/authorization"))
                        .userInfoEndpoint(config -> config.oidcUserService(oidcUserService()))
                        .successHandler(authenticationSuccessHandler)
                )
                .addFilterBefore(exceptionHandlerFilter, OAuth2LoginAuthenticationFilter.class)
                .build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 만약 cors 설정 종류가 많아지면 함수화
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(clientUrl));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> addTextHandler() {
        return registry -> registry.observationConfig().observationHandler(new ObservationTextPublisher());
    }


}
