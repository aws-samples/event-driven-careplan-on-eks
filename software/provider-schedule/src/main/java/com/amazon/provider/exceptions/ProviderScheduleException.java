package com.amazon.provider.exceptions;

public class ProviderScheduleException extends RuntimeException {

    public enum ErrorCode {
        GENERAL("General exception occurred. There is nothing you can do at this time.."),
        NOT_FOUND("Provided resource not found");

        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }
    }

    public ProviderScheduleException(Throwable cause, ErrorCode errorCode) {
        super(errorCode.message, cause);
    }

    public ProviderScheduleException(ErrorCode errorCode) {
        super(errorCode.message);
    }

}
