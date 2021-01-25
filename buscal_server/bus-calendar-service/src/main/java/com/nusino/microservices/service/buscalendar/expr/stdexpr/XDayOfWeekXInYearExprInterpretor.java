/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.XDayOfWeekXInYearExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class XDayOfWeekXInYearExprInterpretor implements ExprInterpretor<XDayOfWeekXInYearExpr> {
    public static final Pattern X_DAY_OF_WEEK_X_IN_YEAR_UPPER_REGEX = Pattern.compile("^([W|E|Y|0-9]{2,})$");
    public static final Pattern NON_DIGIT_REGEX = Pattern.compile("[a-z,A-Z, ,-]+");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return X_DAY_OF_WEEK_X_IN_YEAR_UPPER_REGEX.matcher(exprssionText.toUpperCase()).find() &&
                !D_NORMALIZED_REGEX.matcher(exprssionText).find() &&
                !M_NORMALIZED_REGEX.matcher(exprssionText).find() &&
                Y_REGEX.matcher(exprssionText).find() &&
                W_NUM_REGEX.matcher(exprssionText).find() &&
                E_NUM_REGEX.matcher(exprssionText).find();
    }


    @Override
    public XDayOfWeekXInYearExpr parse(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return null;
        }
        XDayOfWeekXInYearExpr xExpr = new XDayOfWeekXInYearExpr();
        exprssionText = exprssionText.trim();

        exprssionText = exprssionText.replaceAll(S_REGEX.pattern(), "");
        exprssionText = exprssionText.replaceAll(W_NORMALIZED_REGEX.pattern(), "w");
        exprssionText = exprssionText.replaceAll(Y_NORMALIZED_REGEX.pattern(), "y");
        exprssionText = exprssionText.replaceAll(E_NORMALIZED_REGEX.pattern(), "e");

        String value = CommonUtil.fetchFirstNumOnRegexAtGroup(W_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer numOfWeek = Integer.valueOf(value.trim());
            xExpr.setXth(numOfWeek);
        }
        value = CommonUtil.fetchFirstNumOnRegexAtGroup(Y_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer year = Integer.valueOf(value.trim());
        }
        value = CommonUtil.fetchFirstNumOnRegexAtGroup(E_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer dow = Integer.valueOf(value.trim());
            xExpr.setDayOfWeek(dow);
        }
        return xExpr;
    }

    @Override
    public void validate(XDayOfWeekXInYearExpr expr) throws HandlableException {
        List<String> errMsgs = new ArrayList<>();
        if (expr.getXth() == null || expr.getXth() < 1 || expr.getXth() > 54) {
            errMsgs.add("W: Week number of year out of scope, it should be 1 to 53 \n");
        }
        if (expr.getDayOfWeek() == null || expr.getDayOfWeek() < 1 || expr.getDayOfWeek() > 7) {
            errMsgs.add("E: Day of week out of scope, it should be 1 (Monday)  to 7 (Sunday) \n");
        }
        if (errMsgs.size() > 0) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
        }
    }

    @Override
    public String parseParams(XDayOfWeekXInYearExpr funcExpr, String expressions) {
        if (funcExpr == null) {
            funcExpr = new XDayOfWeekXInYearExpr();
        }
        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                funcExpr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public String toExpr(XDayOfWeekXInYearExpr expr) {
        StringBuilder exprText = new StringBuilder();
        exprText.append(String.format("W%sE%sY", expr.getXth(), expr.getDayOfWeek()));
        exprText.append(CommonUtil.toParamsExpr(expr.getParams()));
        return exprText.toString();

    }

    @Override
    public String calculateExpr(XDayOfWeekXInYearExpr expr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(expr.getParams(), date);
        for (LocalDate theDate : dates) {
            boolean isMatched = true;
            LocalDate targetDate = toTargetDate(theDate, expr.getXth(), expr.getDayOfWeek());
            //both is in the some month
            if (theDate.getMonthValue() != targetDate.getMonthValue() || theDate.getDayOfMonth() != targetDate.getDayOfMonth()) {
                isMatched = false;
            }
            if (isMatched) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);
    }

    private LocalDate toTargetDate(LocalDate date, Integer xth, Integer dayOfWeek) {
        Integer year = date.getYear();
        Integer daysToAdd = 7 * (xth - 1);
        LocalDate targetDate = LocalDate.of(year, 1, 1).plus(daysToAdd, ChronoUnit.DAYS);
        int targetDayOfWeek = targetDate.getDayOfWeek().getValue();

        if (dayOfWeek >= targetDayOfWeek) {
            daysToAdd = dayOfWeek - targetDayOfWeek;
        } else {
            daysToAdd = 7 + dayOfWeek - targetDayOfWeek;
        }
        return targetDate.plus(daysToAdd, ChronoUnit.DAYS);
    }
}
