/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class LogicExpr implements Expr {
    private String expression;
    private List<Long> params = new ArrayList<>();
    private boolean isParamAnd = false;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<Long> getParams() {
        return params;
    }

    public void setParams(List<Long> params) {
        this.params = params;
    }

    public boolean isParamAnd() {
        return isParamAnd;
    }

    public void setParamAnd(boolean paramAnd) {
        isParamAnd = paramAnd;
    }

    @Override
    public LogicExpr clone() {
        return JsonUtil.clone(this);
    }
}
