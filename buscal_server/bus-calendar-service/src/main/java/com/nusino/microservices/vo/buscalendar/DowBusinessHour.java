/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.time.LocalTime;

public class DowBusinessHour {
    private Integer dayOfWeek;
    private LocalTime businessTimeFrom;
    private LocalTime businessTimeTo;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
    public DowBusinessHour clone() {
        DowBusinessHour cloned = new DowBusinessHour();
        cloned.dayOfWeek = dayOfWeek;
        cloned.businessTimeFrom = businessTimeFrom;
        cloned.businessTimeTo = businessTimeTo;
        return cloned;
    }
}
