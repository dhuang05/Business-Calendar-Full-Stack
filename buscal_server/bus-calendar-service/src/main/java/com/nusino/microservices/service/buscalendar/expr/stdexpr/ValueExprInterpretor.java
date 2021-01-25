/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.ValueExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

//not use case yet
public class ValueExprInterpretor implements ExprInterpretor<ValueExpr> {
    public static final Pattern NUMBER_REGEX = Pattern.compile("^([\\d]{1,}[.]{0,1}[\\d]{0,})$");
    public static final Pattern BOOLEAN_REGEX = Pattern.compile("^([Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee])$");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return NUMBER_REGEX.matcher(exprssionText).find() || BOOLEAN_REGEX.matcher(exprssionText).find();
    }


    @Override
    public ValueExpr parse(String exprssionText) {
        if (exprssionText == null || exprssionText.trim().isEmpty()) {
            return null;
        }
        exprssionText = exprssionText.toUpperCase();
        ValueExpr valueExpr = new ValueExpr();
        valueExpr.setExpression(exprssionText.trim());
        String value = exprssionText;
        value = value.replaceAll("TRUE", "true");
        value = value.replaceAll("FALSE", "false");
        valueExpr.setValue(value);
        return valueExpr;
    }

    @Override
    public String parseParams(ValueExpr valueExpr, String expressions) {
        if (valueExpr == null) {
            valueExpr = new ValueExpr();
        }
        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                valueExpr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }
    }


    @Override
    public void validate(ValueExpr expr) throws HandlableException {
        // N/A
    }

    @Override
    public String toExpr(ValueExpr expr) {
        StringBuilder exprs = new StringBuilder(" ");
        exprs.append(expr.getValue());
        exprs.append(" ");
        //should apply for logic
        exprs.append(CommonUtil.toParamsExpr(expr.getParams()));
        return exprs.toString();
    }

    @Override
    public String calculateExpr(ValueExpr expr, LocalDate date) {
        return toExpr(expr);
    }
}
