package com.midasdev.mybg.config.security.handler;

import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.ExceptionResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        log.error("Authentication Fail...", e);
        ExceptionResponseWriter.writeException(
                response, ApplicationExceptionType.TOKEN_AUTHENTICATION_EXCEPTION, e.getMessage());
    }
}
