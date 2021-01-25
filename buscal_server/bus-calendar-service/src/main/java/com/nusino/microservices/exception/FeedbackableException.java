/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.exception;

import com.nusino.microservices.vo.buscalendar.Element;

import java.util.List;

public class FeedbackableException extends RuntimeException {
    public CODE code;
    public String[] fields;
    public List<Element> errorElements;

    public FeedbackableException(Throwable ex, CODE code) {
        super(ex);
        this.code = code;
    }

    public FeedbackableException(CODE code, String msg) {
        super(msg);
        this.code = code;
    }

    public FeedbackableException(CODE code, List<Element> errorElements) {
        this.code = code;
        this.errorElements = errorElements;
    }

    public FeedbackableException(CODE code, String msg, String... fields) {
        super(msg);
        this.code = code;
        this.fields = fields;
    }


    public FeedbackableException(CODE code, String msg, Throwable ex) {
        super(msg, ex);
        this.code = code;
    }

    public enum CODE {
        DATA_ERR, NOT_FOUND
    }

    public CODE getCode() {
        return code;
    }

    public void setCode(CODE code) {
        this.code = code;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public List<Element> getErrorElements() {
        return errorElements;
    }

    public void setErrorElements(List<Element> errorElements) {
        this.errorElements = errorElements;
    }
}
