/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;


import java.time.LocalDate;
import java.time.LocalTime;

public class OverridingBusinessHour {
    private LocalDate day;
    private LocalTime businessTimeFrom;
    private LocalTime businessTimeTo;

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public LocalTime getBusinessTimeFrom() {
        return businessTimeFrom;
    }

    public void setBusinessTimeFrom(LocalTime businessTimeFrom) {
        this.businessTimeFrom = businessTimeFrom;
    }

    public LocalTime getBusinessTimeTo() {
        return businessTimeTo;
    }

    public void setBusinessTimeTo(LocalTime businessTimeTo) {
        this.businessTimeTo = businessTimeTo;
    }

    @Override
    public OverridingBusinessHour clone() {
        OverridingBusinessHour cloned = new OverridingBusinessHour();
        cloned.day = day;
        cloned.businessTimeFrom = businessTimeFrom;
        cloned.businessTimeTo = businessTimeTo;
        return cloned;
    }
}
