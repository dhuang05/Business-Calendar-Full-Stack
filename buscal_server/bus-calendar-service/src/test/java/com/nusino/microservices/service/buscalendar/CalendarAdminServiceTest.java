/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.vo.buscalendar.RuleExpr;
import com.nusino.microservices.vo.buscalendar.RuleExprTestResult;
import org.junit.jupiter.api.Test;

public class CalendarAdminServiceTest {

    @Test
    public void testRuleExpression() {
        CalendarAdminService calendarAdminService = new CalendarAdminService();

        RuleExpr ruleExpr = new RuleExpr();
        ruleExpr.setName("Test_ID_1");
        ruleExpr.setExpr("From December 24th To December 24th; December 24th and not Saturday and not Sunday");

        RuleExprTestResult ruleExprTestResult = calendarAdminService.testRuleExpression(ruleExpr, 2020);

    }


}
