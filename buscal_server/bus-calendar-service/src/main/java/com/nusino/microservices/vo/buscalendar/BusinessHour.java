/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nusino.microservices.jsonparser.LocalTimeDeserializer;
import com.nusino.microservices.jsonparser.LocalTimeSerializer;

import java.time.LocalTime;

public class BusinessHour {
    private String dayExpr;
    private Boolean isOverriding = false;
    private String desc;

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime businessHourFrom;

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime businessHourTo;

    public LocalTime getBusinessHourFrom() {
        return businessHourFrom;
    }

    public void setBusinessHourFrom(LocalTime businessHourFrom) {
        this.businessHourFrom = businessHourFrom;
    }

    public LocalTime getBusinessHourTo() {
        return businessHourTo;
    }

    public void setBusinessHourTo(LocalTime businessHourTo) {
        this.businessHourTo = businessHourTo;
    }

    public Boolean getOverriding() {
        return isOverriding;
    }

    public void setOverriding(Boolean overriding) {
        isOverriding = overriding;
    }

    public String getDayExpr() {
        return dayExpr;
    }

    public void setDayExpr(String dayExpr) {
        this.dayExpr = dayExpr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BusinessHour clone() {
        BusinessHour cloned = new BusinessHour();
        cloned.businessHourFrom = businessHourFrom;
        cloned.businessHourTo = businessHourTo;
        cloned.dayExpr = dayExpr;
        cloned.isOverriding = isOverriding;
        cloned.desc = desc;
        return cloned;
    }
}
