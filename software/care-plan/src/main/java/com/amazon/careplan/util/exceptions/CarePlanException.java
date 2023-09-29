package com.amazon.careplan.util.exceptions;

public class CarePlanException extends RuntimeException {

    public enum ErrorCode {
        GENERAL("General exception occurred. There is nothing you can do at this time.."),
        NOT_FOUND("Provided resource not found");

        private final String message;
        ErrorCode(String message) {
            this.message = message;
        }
    }

    public CarePlanException(Throwable cause, ErrorCode errorCode) {
        super(errorCode.message, cause);
    }

    public CarePlanException(ErrorCode errorCode) {
        super(errorCode.message);
    }
}
