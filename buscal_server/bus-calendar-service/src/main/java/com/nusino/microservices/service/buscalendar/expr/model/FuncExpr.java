/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model;

import java.util.List;

public abstract class FuncExpr implements Expr {
    private String id;
    private String expression;
    private boolean isParamAnd = false;
    private List<Long> params;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isParamAnd() {
        return isParamAnd;
    }

    public void setParamAnd(boolean paramAnd) {
        isParamAnd = paramAnd;
    }

    public List<Long> getParams() {
        return params;
    }

    public void setParams(List<Long> params) {
        this.params = params;
    }

    public abstract FuncExpr clone();
}
