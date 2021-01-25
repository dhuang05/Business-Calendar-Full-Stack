/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.exception;

public class AuthorizationException extends RuntimeException {

    public CODE code;

    public AuthorizationException(Throwable ex, CODE code) {
        super(ex);
        this.code = code;
    }

    public AuthorizationException(CODE code, String msg) {
        super(msg);
        this.code = code;
    }

    public AuthorizationException(CODE code, String msg, Throwable ex) {
        super(msg, ex);
        this.code = code;
    }

    public enum CODE {
        UNAUTHORIZED, UNAUTHENTICATED, REQUIRED_LOGIN, DATA_ISSUE
    }
}
