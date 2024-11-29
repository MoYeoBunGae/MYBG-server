package com.midasdev.mochat.global.advice;

import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.midasdev.mochat.global.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    /*
    에러 포맷
    1. httpStatus
    2. errorCode
    3. errorMessage
     */

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleApplicationException(ApplicationException exception) {
        return ResponseEntity.status(exception.getExceptionType().getHttpStatus()).body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        log.error("Exception Occurred...", exception);
        ApplicationExceptionType exceptionType = ApplicationExceptionType.resolveExceptionType(exception);
        return ResponseEntity.status(exceptionType.getHttpStatus()).body(ExceptionResponse.from(exceptionType, exception.getMessage()));
    }

}
