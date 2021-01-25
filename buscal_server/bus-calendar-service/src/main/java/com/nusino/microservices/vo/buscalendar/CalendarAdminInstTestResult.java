/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnership;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarAdminInstTestResult {
    private Boolean success = true;
    private String name;
    private Integer year;
    private Calendar calendar;
    private List<RuleExprError> ruleExprErrors = new ArrayList<>();
    private BusinessCalendarOwnership updatedBusCalOwnership;


    private Date testDate = new Date();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<RuleExprError> getRuleExprErrors() {
        return ruleExprErrors;
    }

    public void setRuleExprErrors(List<RuleExprError> ruleExprErrors) {
        this.ruleExprErrors = ruleExprErrors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public BusinessCalendarOwnership getUpdatedBusCalOwnership() {
        return updatedBusCalOwnership;
    }

    public void setUpdatedBusCalOwnership(BusinessCalendarOwnership updatedBusCalOwnership) {
        this.updatedBusCalOwnership = updatedBusCalOwnership;
    }
}
