/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RuleExprTestResult {
    private Boolean success = true;
    private String name;
    private Integer year;
    private List<LocalDate> ruleDates = new ArrayList<>();
    private RuleExprError ruleExprError;

    private Date testDate = new Date();


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<LocalDate> getRuleDates() {
        return ruleDates;
    }

    public void setRuleDates(List<LocalDate> ruleDates) {
        this.ruleDates = ruleDates;
    }

    public RuleExprError getRuleExprError() {
        return ruleExprError;
    }

    public void setRuleExprError(RuleExprError ruleExprError) {
        this.ruleExprError = ruleExprError;
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
}
