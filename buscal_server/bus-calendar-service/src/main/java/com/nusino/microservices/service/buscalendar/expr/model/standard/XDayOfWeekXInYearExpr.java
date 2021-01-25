/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.FuncExpr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

public class XDayOfWeekXInYearExpr extends FuncExpr {
    private Integer xth;
    private Integer dayOfWeek;

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

    @Override
    public XDayOfWeekXInYearExpr clone() {
        return JsonUtil.clone(this);
    }

}

