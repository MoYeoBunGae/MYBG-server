package com.midasdev.mybg.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ExceptionResponseWriter {

    public static void writeException(
            HttpServletResponse response, ApplicationExceptionType exceptionType, Object... args)
            throws IOException {
        setResponseInfo(response, exceptionType.getHttpStatus());
        PrintWriter writer = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(ExceptionResponse.from(exceptionType, args)));
        writer.flush();
    }

    public static void writeException(HttpServletResponse response, ApplicationException exception)
            throws IOException {
        setResponseInfo(response, exception.getExceptionType().getHttpStatus());
        PrintWriter writer = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(ExceptionResponse.from(exception)));
        writer.flush();
    }

    private static void setResponseInfo(HttpServletResponse response, HttpStatus httpStatus) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }
}
