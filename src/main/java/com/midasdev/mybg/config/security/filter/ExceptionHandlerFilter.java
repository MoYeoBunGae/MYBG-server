package com.midasdev.mybg.config.security.filter;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.ExceptionResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApplicationException e) {
            log.error("before Controller ApplicationException occur!", e);
            ExceptionResponseWriter.writeException(response, e);
        } catch (Exception e) {
            log.error("before Controller Exception occur!", e);
            ExceptionResponseWriter.writeException(response, ApplicationExceptionType.FILTER_OR_API_EXCEPTION, e.getMessage());
        }
    }

}
