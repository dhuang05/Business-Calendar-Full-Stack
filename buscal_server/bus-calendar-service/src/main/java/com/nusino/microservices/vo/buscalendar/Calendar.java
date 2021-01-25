/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calendar {
    private Integer year;
    private String calId;
    private String desc;
    private Boolean isLeapYear;
    private Map<Integer, DowBusinessHour> businessHours = new HashMap<>();
    private Map<LocalDate, OverridingBusinessHour> overridingBusinessHours = new HashMap<>();
    private LocalDate daytimeSavingFrom;
    private LocalDate daytimeSavingTo;
    private ZoneId timeZoneId;
    private Double standardWorkingHoursOfDay;
    private List<Holiday> holidays = new ArrayList<>();
    private List<Day> days = new ArrayList<>();

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getLeapYear() {
        return isLeapYear;
    }

    public void setLeapYear(Boolean leapYear) {
        isLeapYear = leapYear;
    }

    public Map<Integer, DowBusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(Map<Integer, DowBusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    public Map<LocalDate, OverridingBusinessHour> getOverridingBusinessHours() {
        return overridingBusinessHours;
    }

    public void setOverridingBusinessHours(Map<LocalDate, OverridingBusinessHour> overridingBusinessHours) {
        this.overridingBusinessHours = overridingBusinessHours;
    }

    public LocalDate getDaytimeSavingFrom() {
        return daytimeSavingFrom;
    }

    public void setDaytimeSavingFrom(LocalDate daytimeSavingFrom) {
        this.daytimeSavingFrom = daytimeSavingFrom;
    }

    public LocalDate getDaytimeSavingTo() {
        return daytimeSavingTo;
    }

    public void setDaytimeSavingTo(LocalDate daytimeSavingTo) {
        this.daytimeSavingTo = daytimeSavingTo;
    }

    public ZoneId getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(ZoneId timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public List<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getStandardWorkingHoursOfDay() {
        return standardWorkingHoursOfDay;
    }

    public void setStandardWorkingHoursOfDay(Double standardWorkingHoursOfDay) {
        this.standardWorkingHoursOfDay = standardWorkingHoursOfDay;
    }
}
