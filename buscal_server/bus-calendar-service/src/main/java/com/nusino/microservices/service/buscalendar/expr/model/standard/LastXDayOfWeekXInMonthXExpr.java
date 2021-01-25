/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.FuncExpr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

public class LastXDayOfWeekXInMonthXExpr extends FuncExpr {
    private Integer xth;
    private Integer dayOfWeek;
    private Integer month;

    public Integer getXth() {
        return xth;
    }

    public void setXth(Integer xth) {
        this.xth = xth;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Override
    public LastXDayOfWeekXInMonthXExpr clone() {
        return JsonUtil.clone(this);
    }
}
