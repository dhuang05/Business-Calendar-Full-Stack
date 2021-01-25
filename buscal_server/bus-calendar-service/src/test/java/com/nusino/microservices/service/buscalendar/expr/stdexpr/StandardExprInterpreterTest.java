/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.service.buscalendar.ExprEngine;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.*;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.CustomFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.GoodFridayFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.CustomFuncExprInterpretor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class StandardExprInterpreterTest {
    private final ExprEngine exprEngine = new ExprEngine(true);

    @Test
    public void testDateExpr() {
        DateExprInterpretor dateExprInterpreter = new DateExprInterpretor();
        DateExpr dateExpr = null;
        dateExpr = dateExprInterpreter.parse("M4D1E3");
        //dateExpr.setParamAnd(true);
        dateExprInterpreter.parseParams(dateExpr, "(1)");
        //println!("to Expr = {}", gf.to_expr());
        LocalDate date = LocalDate.of(2020, 04, 2);
        String result = dateExprInterpreter.calculateExpr(dateExpr, date);
        Assertions.assertEquals("true", result);
        //

        dateExpr = dateExprInterpreter.parse("M4D1E3");
        dateExprInterpreter.parseParams(dateExpr, "{2}");
        //println!("to Expr = {}", gf.to_expr());
        date = LocalDate.of(2020, 04, 2);
        result = dateExprInterpreter.calculateExpr(dateExpr, date);
        Assertions.assertEquals("false", result);
    }


    //@Test
    public void testCustomFuncExpr() {
        String expressions = "function goodFriday(Y){\n" +
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


        CustomFuncExprInterpretor funcExprInterpretor = new CustomFuncExprInterpretor(exprEngine, true);
        if (funcExprInterpretor.isMyExpr(expressions)) {
            CustomFuncExpr customFuncExpr = funcExprInterpretor.parse(expressions);
            String valText = funcExprInterpretor.calculateExpr(customFuncExpr, LocalDate.of(2020, 4, 12));
            System.out.println(funcExprInterpretor.toExpr(customFuncExpr) + "\n value = " + valText);
            Assertions.assertEquals("true", valText);
        }
    }


    @Test
    public void testLogicExpr() {
        LogicExprInterpretor interpretor = new LogicExprInterpretor();
        String expr = "|| M12D26E6(2) || M12D25E6(2) || M12D26E7(2) || M12D25E7(2)";
        if (interpretor.isMyExpr(expr)) {
            LogicExpr logicExpr = interpretor.parse(expr);
            Assertions.assertEquals("||", interpretor.calculateExpr(logicExpr, null).trim());
        }

    }

    @Test
    public void testValueExpr() {
        ValueExprInterpretor interpretor = new ValueExprInterpretor();
        String expr = "1.5";
        ValueExpr valueExpr = interpretor.parse(expr);
        Assertions.assertEquals("1.5", interpretor.calculateExpr(valueExpr, null).trim());

        expr = "true";
        valueExpr = interpretor.parse(expr);
        Assertions.assertEquals("true", interpretor.calculateExpr(valueExpr, null).trim());

        expr = "TRUE";
        valueExpr = interpretor.parse(expr);
        Assertions.assertEquals("true", interpretor.calculateExpr(valueExpr, null).trim());

        expr = "false";
        valueExpr = interpretor.parse(expr);
        Assertions.assertEquals("false", interpretor.calculateExpr(valueExpr, null).trim());
    }


    @Test
    public void testXthDayOfWeekInMonth() {
        XDayOfWeekXInMonthXExprInterpretor interpreter = new XDayOfWeekXInMonthXExprInterpretor();
        XDayOfWeekXInMonthXExpr expr = interpreter.parse("W4E1M4");
        interpreter.parseParams(expr, "(2)");
        //println!("to Expr = {}", gf.to_expr());
        LocalDate date = LocalDate.of(2020, 04, 29);
        String result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);

        //
        expr = interpreter.parse("W4E1M");
        //println!("to Expr = {}", gf.to_expr());
        date = LocalDate.of(2020, 05, 25);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);

        //
        interpreter.parseParams(expr, "(3)");
        //println!("to Expr = {}", gf.to_expr());
        date = LocalDate.of(2020, 05, 25);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("false", result);

    }

    @Test
    public void testLastDayOfMonth() {
        XDayOfWeekXInMonthXExprInterpretor interpreter = new XDayOfWeekXInMonthXExprInterpretor();
        XDayOfWeekXInMonthXExpr expr = interpreter.parse("W4E1M4");
        interpreter.parseParams(expr, "(2)");
        //println!("to Expr = {}", gf.to_expr());
        LocalDate date = LocalDate.of(2020, 04, 29);
        String result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);

        //
        expr = interpreter.parse("W4E1M");
        //println!("to Expr = {}", gf.to_expr());
        date = LocalDate.of(2020, 05, 25);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);

        //
        interpreter.parseParams(expr, "(3)");
        //println!("to Expr = {}", gf.to_expr());
        date = LocalDate.of(2020, 05, 25);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("false", result);

    }

    @Test
    public void testXthDayOfWeekInYear() {
        XDayOfWeekXInYearExprInterpretor interpreter = new XDayOfWeekXInYearExprInterpretor();
        XDayOfWeekXInYearExpr expr = interpreter.parse("W5E2Y");
        interpreter.parseParams(expr, "(2)");
        LocalDate date = LocalDate.of(2020, 02, 6);
        String result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);
        //
        expr = interpreter.parse("W7E5Y");
        interpreter.parseParams(expr, "(1,2)");
        date = LocalDate.of(2020, 02, 15);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);
        //
        date = LocalDate.of(2020, 02, 16);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("true", result);
        //
        date = LocalDate.of(2020, 02, 14);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("false", result);
        //
        date = LocalDate.of(2020, 02, 17);
        result = interpreter.calculateExpr(expr, date);
        Assertions.assertEquals("false", result);

    }
}
