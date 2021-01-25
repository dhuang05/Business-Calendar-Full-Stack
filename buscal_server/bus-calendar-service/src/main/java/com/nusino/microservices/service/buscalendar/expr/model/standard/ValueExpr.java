/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValueExpr implements Expr {
    private String id;
    private String expression;
    private String value;
    private Boolean isParamAnd;
    private List<Long> params = new ArrayList<>();

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getParamAnd() {
        return isParamAnd;
    }

    public void setParamAnd(Boolean paramAnd) {
        isParamAnd = paramAnd;
    }

    public List<Long> getParams() {
        return params;
    }

    public void setParams(List<Long> params) {
        this.params = params;
    }

    @Override
    public ValueExpr clone() {
        return JsonUtil.clone(this);
    }
}
