/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;


public class DateExpr implements Expr {
    private String id;
    private String expression;
    private List<Long> params = new ArrayList<>();
    private boolean isParamAnd = false;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer dayOfWeek;

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public DateExpr clone() {
        return JsonUtil.clone(this);
    }
}
