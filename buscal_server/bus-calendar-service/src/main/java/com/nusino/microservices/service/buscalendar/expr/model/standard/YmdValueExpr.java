/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

public class YmdValueExpr implements Expr {
    private String id;
    private Integer year;
    private Integer month;
    private Integer day;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public YmdValueExpr clone() {
        return JsonUtil.clone(this);
    }

}
