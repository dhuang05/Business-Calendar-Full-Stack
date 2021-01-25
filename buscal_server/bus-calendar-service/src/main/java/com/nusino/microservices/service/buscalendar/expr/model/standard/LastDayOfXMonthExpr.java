/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.model.standard;

import com.nusino.microservices.service.buscalendar.expr.model.FuncExpr;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;

public class LastDayOfXMonthExpr extends FuncExpr {
    private Integer month;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Override
    public LastDayOfXMonthExpr clone() {
        return JsonUtil.clone(this);
    }
}
