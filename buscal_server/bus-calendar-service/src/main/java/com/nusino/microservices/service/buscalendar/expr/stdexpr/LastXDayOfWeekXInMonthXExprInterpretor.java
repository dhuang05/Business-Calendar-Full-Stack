/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.LastXDayOfWeekXInMonthXExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LastXDayOfWeekXInMonthXExprInterpretor implements ExprInterpretor<LastXDayOfWeekXInMonthXExpr> {
    public static final Pattern LAST_X_DAY_OF_WEEK_X_IN_MONTH_X_FUNC_UPPER_REGEX = Pattern.compile("^(L[W|E|M|0-9]{2,})$");
    public static final Pattern NON_DIGIT_REGEX = Pattern.compile("[a-z,A-Z, ,-]+");
    public static final Pattern S_REGEX = Pattern.compile("\\s+");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return LAST_X_DAY_OF_WEEK_X_IN_MONTH_X_FUNC_UPPER_REGEX.matcher(exprssionText.toUpperCase()).find() &&
                !D_NORMALIZED_REGEX.matcher(exprssionText).find() &&
                !Y_NORMALIZED_REGEX.matcher(exprssionText).find() &&
                M_0_REGEX.matcher(exprssionText).find() &&
                W_NUM_REGEX.matcher(exprssionText).find() &&
                E_NUM_REGEX.matcher(exprssionText).find();
    }


    @Override
    public LastXDayOfWeekXInMonthXExpr parse(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return null;
        }
        exprssionText = exprssionText.trim().toUpperCase();
        LastXDayOfWeekXInMonthXExpr xExpr = new LastXDayOfWeekXInMonthXExpr();
        //
        exprssionText = exprssionText.replaceAll(S_REGEX.pattern(), "");
        exprssionText = exprssionText.replaceAll(W_NORMALIZED_REGEX.pattern(), "w");
        exprssionText = exprssionText.replaceAll(M_NORMALIZED_REGEX.pattern(), "m");
        exprssionText = exprssionText.replaceAll(E_NORMALIZED_REGEX.pattern(), "e");
        //
        StringBuilder errMsg = new StringBuilder();
        String value = CommonUtil.fetchFirstNumOnRegexAtGroup(W_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer numOfWeek = Integer.valueOf(value.trim());
            xExpr.setXth(numOfWeek);
        }
        value = CommonUtil.fetchFirstNumOnRegexAtGroup(M_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer month = Integer.valueOf(value.trim());
            xExpr.setMonth(month);
        }

        value = CommonUtil.fetchFirstNumOnRegexAtGroup(E_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer dow = Integer.valueOf(value.trim());
            xExpr.setDayOfWeek(dow);
        }

        return xExpr;
    }

    @Override
    public void validate(LastXDayOfWeekXInMonthXExpr expr) throws HandlableException {
        List<String> errMsgs = new ArrayList<>();
        if (expr.getXth() == null || (expr.getXth() != null && expr.getXth() < 1 || expr.getXth() > 5)) {
            errMsgs.add("W: Week number of month out of scope, it should be 1 to 5 \n");
        }
        if (expr.getMonth() != null && (expr.getMonth() < 0 || expr.getMonth() > 12)) {
            errMsgs.add("M: Month of year out of scope, it should be 0 (every month), and (January)  to 12 (December) \n");
        }
        if (expr.getDayOfWeek() == null || (expr.getDayOfWeek() < 1 || expr.getDayOfWeek() > 7)) {
            errMsgs.add("E: Day of week out of scope, it should be 1 (Monday)  to 7 (Sunday) \n");
        }
        if (errMsgs.size() > 0) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
        }
    }

    @Override
    public String parseParams(LastXDayOfWeekXInMonthXExpr xExpr, String expressions) {
        if (xExpr == null) {
            xExpr = new LastXDayOfWeekXInMonthXExpr();
        }
        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                xExpr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public String toExpr(LastXDayOfWeekXInMonthXExpr expr) {
        StringBuilder exprText = new StringBuilder();
        String month = "";
        if (expr.getMonth() != null && expr.getMonth() != 0) {
            month = String.valueOf(expr.getMonth());
        }
        exprText.append(String.format("LW%sE%sM%s", expr.getXth(), expr.getDayOfWeek(), month));
        exprText.append(CommonUtil.toParamsExpr(expr.getParams()));
        return exprText.toString();
    }

    @Override
    public String calculateExpr(LastXDayOfWeekXInMonthXExpr expr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(expr.getParams(), date);
        for (LocalDate theDate : dates) {
            boolean isMatched = true;
            //that day should be some month if defined
            if (expr.getMonth() != null && expr.getMonth() != theDate.getMonthValue()) {
                isMatched = false;
            }
            if (isMatched) {
                LocalDate targetDate = toTargetDate(theDate, expr.getXth(), expr.getDayOfWeek(), expr.getMonth());
                //both is in the some month
                if (theDate.getMonthValue() != targetDate.getMonthValue() || theDate.getDayOfMonth() != targetDate.getDayOfMonth()) {
                    isMatched = false;
                }
            }
            if (isMatched) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);

    }

    public LocalDate toTargetDate(LocalDate date, Integer xth, Integer dayOfWeek, Integer month) {

        Integer year = date.getYear();
        if (month == null) {
            month = date.getMonthValue();
        }
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
        //last day of month + 1
        int count = 0;
        LocalDate targetDate = LocalDate.of(year, month, 1);
        for (int i = 0; i < 7; i++) {
            targetDate = targetDate.plus(-1, ChronoUnit.DAYS);
            if (targetDate.getDayOfWeek().getValue() == dayOfWeek) {
                targetDate = targetDate.plus(-1 * (xth - 1) * 7, ChronoUnit.DAYS);
                break;
            }
        }
        return targetDate;
    }
}
