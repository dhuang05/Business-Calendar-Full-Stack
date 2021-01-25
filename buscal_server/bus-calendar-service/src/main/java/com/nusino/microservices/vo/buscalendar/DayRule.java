/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import com.nusino.microservices.service.buscalendar.util.JsonUtil;

import java.time.LocalDate;

public class DayRule {
    private String dayRuleId;
    private String desc;
    private String expr;
    private LocalDate effectiveDate;
    private LocalDate expiredDate;

    public String getDayRuleId() {
        return dayRuleId;
    }

    public void setDayRuleId(String dayRuleId) {
        this.dayRuleId = dayRuleId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public DayRule clone() {
        return JsonUtil.clone(this);
    }
}
