/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.util.ArrayList;
import java.util.List;

public class CalendarInst {
    private String calId;
    private String desc;
    private String timeZone;
    private List<BusinessHour> businessHours = new ArrayList<>();
    private List<DayRule> holidayRules = new ArrayList<>();

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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<BusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    public List<DayRule> getHolidayRules() {
        return holidayRules;
    }

    public void setHolidayRules(List<DayRule> holidayRules) {
        this.holidayRules = holidayRules;
    }

    public CalendarInst clone() {
        CalendarInst cloned = new CalendarInst();
        cloned.calId = calId;
        cloned.desc = desc;
        cloned.timeZone = timeZone;
        for (BusinessHour businessHour : businessHours) {
            cloned.businessHours.add(businessHour.clone());
        }
        for (DayRule dayRule : holidayRules) {
            cloned.holidayRules.add(dayRule.clone());
        }
        return cloned;
    }
}
