package com.loanmanagement.common.exception;

/**
 * Exception thrown when authentication or authorization fails
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
