/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

public class Element {
    private String error;
    private ElementType type = ElementType.EXPR;
    private String text;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean hasContent() {
        return text == null || text.trim().isEmpty();
    }

    public void addError(String error) {
        if (this.error == null) {
            this.error = error;
        } else {
            this.error = ";" + error;
        }
    }

    public void append(String chars) {
        if (chars == null) {
            return;
        }
        if (text == null) {
            text = chars;
        } else {
            text += chars;
        }
    }

    public void append(char ch) {
        if (text == null) {
            text = String.valueOf(ch);
        } else {
            text += ch;
        }
    }

    public enum ElementType {
        DATE_RANGE, LOGIC, EXPR, FUNC, PARAM
    }
}

