/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.time.LocalDate;

public class Holiday {
    public String ruleId;
    public String name;
    public LocalDate day;


    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    @Override
    public Holiday clone() {
        Holiday cloned = new Holiday();
        cloned.ruleId = ruleId;
        cloned.name = name;
        cloned.day = day;
        return cloned;
    }
}
