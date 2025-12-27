package com.midasdev.mybg.global.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ApplicationExceptionType exceptionType;
    private final Object[] messageArguments;

    public ApplicationException(ApplicationExceptionType exceptionType, Object... args) {
        super(exceptionType.getErrorMessage(args));
        this.exceptionType = exceptionType;
        this.messageArguments = args;
    }

    public ApplicationException(
            ApplicationExceptionType exceptionType, Throwable cause, Object... args) {
        super(exceptionType.getErrorMessage(args), cause);
        this.exceptionType = exceptionType;
        this.messageArguments = args;
    }

    public String getMessage() {
        return exceptionType.getErrorMessage(messageArguments);
    }
}
