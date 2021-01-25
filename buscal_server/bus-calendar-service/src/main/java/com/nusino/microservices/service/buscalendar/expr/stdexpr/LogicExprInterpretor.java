/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.LogicExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class LogicExprInterpretor implements ExprInterpretor<LogicExpr> {
    public static final Pattern LOGIC_REGEX = Pattern.compile("^([\\||&|!|>|<|=|(|)]{1,})$");
    //public static final Pattern LOGIC_REGEX = Pattern.compile("^([\\||&|!|>|<|=|(|)|+|-|*|/]{1,})$");
    public static final Pattern AND_REGEX = Pattern.compile("[&]{1,}");
    public static final Pattern OR_REGEX = Pattern.compile("[|]{1,}");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return LOGIC_REGEX.matcher(exprssionText).find();
    }

    @Override
    public LogicExpr parse(String exprssionText) {
        if (exprssionText == null || exprssionText.trim().isEmpty()) {
            return null;
        }
        exprssionText = exprssionText.replaceAll(AND_REGEX.pattern(), " && ");
        exprssionText = exprssionText.replaceAll(OR_REGEX.pattern(), " || ");

        LogicExpr logicExpr = new LogicExpr();
        logicExpr.setExpression(exprssionText);
        return logicExpr;
    }

    //not used
    @Override
    public String parseParams(LogicExpr expr, String expressions) {
        if (expr == null) {
            expr = new LogicExpr();
        }

        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                expr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public void validate(LogicExpr expr) throws HandlableException {
        //N/A
    }


    @Override
    public String toExpr(LogicExpr expr) {
        return expr.getExpression();
    }

    @Override
    public String calculateExpr(LogicExpr expr, LocalDate date) {
        return toExpr(expr);
    }

    @Override
    public String toExprString(Expr expr) {
        return toExpr((LogicExpr) expr);
    }

    @Override
    public String calculateExprString(Expr expr, LocalDate date) {
        return toExpr((LogicExpr) expr);
    }
}
