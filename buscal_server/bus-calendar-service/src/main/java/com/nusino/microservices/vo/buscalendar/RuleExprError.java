/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.vo.buscalendar;

import java.util.List;

public class RuleExprError {
    private String ruleType;
    private String exprName;
    private List<Element> elementErrors;

    public String getExprName() {
        return exprName;
    }

    public void setExprName(String exprName) {
        this.exprName = exprName;
    }

    public List<Element> getElementErrors() {
        return elementErrors;
    }

    public void setElementErrors(List<Element> elementErrors) {
        this.elementErrors = elementErrors;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
}
