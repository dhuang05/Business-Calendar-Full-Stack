/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.exception;

public class HandlableException extends Exception {
    public HandlableException(Throwable ex) {
        super(ex);
    }

    public HandlableException(String msg) {
        super(msg);
    }

    public HandlableException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
