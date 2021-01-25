/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr;

import com.nusino.microservices.service.buscalendar.ExprEngine;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.GoodFridayFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin.EasterMondayFunction;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin.EasterSundayFunction;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin.GoodFridayFunction;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin.MardiGrasDayFunction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DayRuleExprInterpretorTest {
    private final ExprEngine exprEngine = new ExprEngine(true);


    @Test
    public void testLastWeekOfDayOfMonthExprInterpretorCase() {

        String exprText = "03-1,03-31; LW1E2M3";
        LocalDate date = LocalDate.of(2021, 03, 30);
        evaluate(0, exprText, date);
        //
        exprText = "03-1,03-31; LW1E3M3";
        date = LocalDate.of(2021, 03, 31);
        evaluate(0, exprText, date);

        exprText = "03-1,03-31; LW2E3M3";
        date = LocalDate.of(2021, 03, 24);
        evaluate(0, exprText, date);
    }


    @Test
    public void testDayRuleExprInterpretorCase() {

        String exprText = "03-20,04-25; Good Friday";
        LocalDate date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);
    }

    @Test
    public void testDayExprComplexCase() {
        String exprText = "12-25,12-28;(M12D25&&!E6&!E7)||(M12d26&&!E6&&!E7)||M12D26E6(2)||M12D25E6(2)||M12D26E7(2)||M12D25E7(2)";
        LocalDate date = LocalDate.of(2020, 12, 25);
        evaluate(0, exprText, date);
    }

    @Test
    public void testChineseCalendar () {
        DayRuleExprInterpretor interpretor = exprEngine.getDayRuleExprInterpretor();
        String exprText = null;
        LocalDate date = null;

        exprText = "chinese 1 / 2";
        date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);

        exprText = "chinese new year";
        date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);
        //
        exprText = "chinese   new   year  eve";
        date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);
        //
        exprText = "chinese qingming";
        date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);

        exprText = "chinese 1/2";
        date = LocalDate.of(2020, 04, 10);
        evaluate(0, exprText, date);
    }

    @Test
    public void testDayExprParse() {
        LocalDate local = LocalDate.now();
        LocalDate today = LocalDate.of(local.getYear(), local.getMonth(), local.getDayOfMonth());

        long t0 = System.currentTimeMillis();
        DayRuleExprInterpretor interpretor = exprEngine.getDayRuleExprInterpretor();
        String exprText = null;

        exprText = "Mardi Gras Day";
        evaluate(8, exprText, LocalDate.of(2028, 2, 29));

        exprText = "EASTER sunday && E7";

        exprText = "Good Friday(-45)";
        evaluate(8, exprText, LocalDate.of(2028, 2, 29));

        exprText = "Good Friday (-45)";
        evaluate(8, exprText, LocalDate.of(2028, 2, 29));

        exprText = "EASTer sunday (-47)";
        evaluate(8, exprText, LocalDate.of(2028, 2, 29));

        exprText = "EASTer monday(-48)";
        evaluate(8, exprText, LocalDate.of(2028, 2, 29));


        exprText = "(M01d01 && !E6 && !E7)  && true || (M01d01(1, 2) && E1)";
        evaluate(10, exprText, LocalDate.of(2020, 1, 1));

        exprText = "1-2,3-4; (M01d01 && !E6 && !E7) || LW2E3M6(1) || (M01d01(1, 2) && E1)";
        evaluate(20, exprText, today);

        exprText = "3-20,4-28; Good Friday";
        evaluate(30, exprText, today);


        exprText = "2-14,2-29; W3E1M";
        evaluate(40, exprText, today);

        exprText = "2-14,2-29; LW3E1M";
        evaluate(40, exprText, today);

        exprText = "5-18; MM05dd24(0,-1,-2,-3,-4,-5,-6) && E1";
        evaluate(50, exprText, today);

        exprText = "M05dd01(-4,-5,-6) && E2";
        evaluate(60, exprText, today);

        exprText = "7-1, 7-3; (mm07dd01 && !E6 && !E7) || (Mm07dd01(1, 2) && E1)";
        evaluate(70, exprText, today);

        exprText = "8-1,8-7; W1E1M";
        evaluate(80, exprText, today);

        exprText = "9-1,9-7; W1E1M(-12,-4)";
        evaluate(90, exprText, today);

        exprText = "10-7,10-18; W2E1M10";
        evaluate(100, exprText, today);

        exprText = "10-7,10-18; W2E1M10(10)";
        evaluate(110, exprText, today);


        exprText = "10-7,10-18; LW2E1M10(10)";
        evaluate(110, exprText, today);

        exprText = "10-7,10-18; Good Friday(-1)";
        evaluate(120, exprText, today);

        exprText = "11-11,11-11; m11d11";
        evaluate(130, exprText, today);

        exprText = "W3E1Y";
        evaluate(140, exprText, today);

        exprText = "W3E1Y (1,2)";
        evaluate(150, exprText, today);

        exprText = "LDM4";
        evaluate(160, exprText, today);

        exprText = "LDM (4)";
        evaluate(170, exprText, today);

        exprText = "12-25,12-28; (m12d25 && !e6 & !e7) | (m12d26 && !e6 && !e7) & Good Friday(-1,-2,-4) & W2E1M10(-1,-2,-4) | LW2E1M10(-1,-2,-4) | M12D26E6(2) |  W3E1Y(-1,-2,-4) & m12d25e6(2) | m12d26e7(2) | m12d25e7(2,-4,-5,-6,-7,-8,-10) WHO I AM";
        evaluateWithError(180, exprText, today);

        exprText = "3-25,4-28; function goodFriday(Y) {\n" +
                "    var C = Math.floor(Y/100);\n" +
                "    var N = Y - 19*Math.floor(Y/19);\n" +
                "    var K = Math.floor((C - 17)/25);\n" +
                "    var I = C - Math.floor(C/4) - Math.floor((C - K)/3) + 19*N + 15;\n" +
                "    I = I - 30*Math.floor((I/30));\n" +
                "    I = I - Math.floor(I/28)*(1 - Math.floor(I/28)*Math.floor(29/(I + 1))*Math.floor((21 - N)/11));\n" +
                "    var J = Y + Math.floor(Y/4) + I + 2 - C + Math.floor(C/4);\n" +
                "    J = J - 7*Math.floor(J/7);\n" +
                "    var L = I - J;\n" +
                "    var M = 3 + Math.floor((L + 40)/44);\n" +
                "    var D = L + 28 - 31*Math.floor(M/4);\n" +
                "    D = D-2;\n" +
                "    if (D <= 0){\n" +
                "    D = D + 31;\n" +
                "    M = 3;\n" +
                "    }\n" +
                "    return Y + '-' + parseInt(M, 10) + '-' + parseInt(D, 10);\n" +
                "}(1,2,3)";
        evaluate(400, exprText, LocalDate.of(2020, 4, 12));
        long t1 = System.currentTimeMillis();
        System.out.printf("time spend %s sec \n", (t1 - t0) / 1000000000);
    }

    private void evaluate(int index, String exprText, LocalDate day) {
        DayRuleExprInterpretor interpretor = exprEngine.getDayRuleExprInterpretor();
        System.out.printf("////%s data = %s \n input/out:\n%s\n", index, day.toString(), exprText);
        DayRuleExpr dayRuleExpr = interpretor.parse(exprText);
        String formula = interpretor.calculateExpr(dayRuleExpr, day);
        System.out.printf("%s \nrslt %s = %s\n", interpretor.toExpr(dayRuleExpr), formula, exprEngine.evalBooleanFormula(formula, exprText));
    }

    private void evaluateWithError(int index, String exprText, LocalDate day) {
        try {
            DayRuleExprInterpretor interpretor = exprEngine.getDayRuleExprInterpretor();
            System.out.printf("////%s data = %s \n input/out:\n%s\n", index, day.toString(), exprText);
            DayRuleExpr dayRuleExpr = interpretor.parse(exprText);
            String formula = interpretor.calculateExpr(dayRuleExpr, day);
            System.out.printf("%s \nrslt %s = %s\n", interpretor.toExpr(dayRuleExpr), formula, exprEngine.evalBooleanFormula(formula, exprText));
        } catch (Exception ex) {
            System.out.printf("Error: \n" + exprText + "\n" + ex.getMessage() + "\n");

        }
    }

}
