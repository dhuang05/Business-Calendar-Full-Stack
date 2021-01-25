/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class Day {
    private LocalDate date;
    private LocalTime businessTimeFrom;
    private LocalTime businessTimeTo;
    private Short dow;
    private Boolean isBusinessDay;
    private Boolean isDayLightSaving;
    private Holiday holiday;
    private Boolean isOverridingWorkingHour;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Short getDow() {
        return dow;
    }

    public void setDow(Short dow) {
        this.dow = dow;
    }

    public Boolean getBusinessDay() {
        return isBusinessDay;
    }

    public void setBusinessDay(Boolean businessDay) {
        this.isBusinessDay = businessDay;
    }

    public Boolean getDayLightSaving() {
        return isDayLightSaving;
    }

    public void setDayLightSaving(Boolean dayLightSaving) {
        this.isDayLightSaving = dayLightSaving;
    }

    public Holiday getHoliday() {
        return holiday;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }

    public Boolean getOverridingWorkingHour() {
        return isOverridingWorkingHour;
    }

    public void setOverridingWorkingHour(Boolean overridingWorkingHour) {
        this.isOverridingWorkingHour = overridingWorkingHour;
    }

    @Override
    public Day clone() {
        Day cloned = new Day();
        cloned.date = date;
        cloned.businessTimeFrom = businessTimeFrom;
        cloned.businessTimeTo = businessTimeTo;
        cloned.dow = dow;
        cloned.isBusinessDay = isBusinessDay;
        cloned.isDayLightSaving = isDayLightSaving;
        cloned.isOverridingWorkingHour = isOverridingWorkingHour;
        if (holiday != null) {
            cloned.holiday = holiday.clone();
        }
        return cloned;
    }
}
