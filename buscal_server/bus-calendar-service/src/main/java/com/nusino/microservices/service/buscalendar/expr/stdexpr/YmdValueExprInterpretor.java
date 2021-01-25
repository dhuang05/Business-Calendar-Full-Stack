/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.YmdValueExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class YmdValueExprInterpretor implements ExprInterpretor<YmdValueExpr> {
    public static final Pattern YMD_VALUE_REGEX = Pattern.compile("^([\\d|,|.|\\-|\\s]{1,}[;]{1,})$");
    private static final Pattern DASH_REGEX = Pattern.compile("[-]{1,}");
    private static final Pattern S_REGEX = Pattern.compile("\\s+");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return YMD_VALUE_REGEX.matcher(exprssionText).find();
    }

    @Override
    public String parseParams(YmdValueExpr expr, String expressions) {
        return expressions;
    }

    @Override
    public YmdValueExpr parse(String exprssionText) {
        if (exprssionText == null || exprssionText.trim().isEmpty()) {
            return null;
        }
        YmdValueExpr YmdValueExpr = new YmdValueExpr();
        exprssionText = exprssionText.replaceAll(S_REGEX.pattern(), "").trim();
        String[] parts = DASH_REGEX.split(exprssionText);
        if (parts.length - 3 >= 0) {
            YmdValueExpr.setYear(Integer.valueOf(parts[parts.length - 3]));
        }
        if (parts.length - 2 >= 0) {
            YmdValueExpr.setMonth(Integer.valueOf(parts[parts.length - 2]));
        }
        if (parts.length - 1 >= 0) {
            YmdValueExpr.setDay(Integer.valueOf(parts[parts.length - 1]));
        }
        return YmdValueExpr;
    }

    @Override
    public void validate(YmdValueExpr expr) throws HandlableException {
        List<String> errMsgs = new ArrayList<>();
        if (expr.getYear() != null && (expr.getYear() < 0 || expr.getYear() > 10000)) {
            errMsgs.add("Y: year out of scope\n");
        }
        if (expr.getMonth() != null && (expr.getMonth() < 0 || expr.getMonth() > 12)) {
            errMsgs.add("M: Month of year out of scope, it should be 0 (every month), and (January)  to 12 (December) \n");
        }
        if (expr.getDay() != null && (expr.getDay() < 1 || expr.getDay() > 31)) {
            errMsgs.add("M: Day of Month out of scope, it should be 0 to 31 (December) \n");
        }
        if (errMsgs.size() > 0) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
        }
    }

    @Override
    public String toExpr(YmdValueExpr expr) {
        StringBuilder exprs = new StringBuilder();
        boolean entered = false;
        if (expr.getYear() != null) {
            entered = true;
            exprs.append(expr.getYear());
        }
        if (expr.getMonth() != null) {
            if (entered) {
                exprs.append("-");
            }
            entered = true;
            exprs.append(expr.getMonth());
        }

        if (entered) {
            exprs.append("-");
        }
        exprs.append(expr.getDay());
        return exprs.toString();
    }

    @Override
    public String calculateExpr(YmdValueExpr expr, LocalDate date) {
        return "";
    }


    public static Integer asYearMonthDay(YmdValueExpr ymdValueExpr) {
        Integer ymd = ymdValueExpr.getDay();
        if (ymdValueExpr.getMonth() != null) {
            ymd = ymd + ymdValueExpr.getMonth() * 100;
        }
        if (ymdValueExpr.getYear() != null) {
            ymd = ymd + ymdValueExpr.getYear() * 10000;
        }
        return ymd;
    }

    public static Integer toYearMonthDayOf(YmdValueExpr ymdValueExpr, LocalDate date) {
        Integer ymd = date.getDayOfMonth();
        if (ymdValueExpr.getMonth() != null) {
            ymd = ymd + date.getMonthValue() * 100;
        }
        if (ymdValueExpr.getYear() != null) {
            ymd = ymd + date.getYear() * 10000;
        }
        return ymd;
    }
}
