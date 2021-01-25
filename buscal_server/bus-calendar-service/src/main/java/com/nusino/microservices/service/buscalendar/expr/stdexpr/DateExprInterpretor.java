/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.DateExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DateExprInterpretor implements ExprInterpretor<DateExpr> {
    public static final Pattern DATE_REGEX = Pattern.compile("^([Y|y|M|m|D|d|E|e|0-9]{1,})$");
    public static Pattern NAME_NUM_PAIR_REGEX = Pattern.compile("([Y|y|M|m|D|d|E|e]{1,}[0-9]{1,}){1,}");

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return DATE_REGEX.matcher(exprssionText).find() &&
                !W_NORMALIZED_REGEX.matcher(exprssionText).find() &&
                (Y_NUM_REGEX.matcher(exprssionText).find()
                        || M_NUM_REGEX.matcher(exprssionText).find()
                        || D_NUM_REGEX.matcher(exprssionText).find()
                        || E_NUM_REGEX.matcher(exprssionText).find());
    }

    @Override
    public DateExpr parse(String exprssionText) {
        DateExpr dateExpr = null;
        if (exprssionText == null || exprssionText.trim().isEmpty()) {
            return null;
        }
        exprssionText = exprssionText.trim();
        dateExpr = new DateExpr();

        exprssionText = exprssionText.replaceAll(S_REGEX.pattern(), "");
        exprssionText = exprssionText.replaceAll(Y_NORMALIZED_REGEX.pattern(), "y");
        exprssionText = exprssionText.replaceAll(M_NORMALIZED_REGEX.pattern(), "m");
        exprssionText = exprssionText.replaceAll(D_NORMALIZED_REGEX.pattern(), "d");
        exprssionText = exprssionText.replaceAll(E_NORMALIZED_REGEX.pattern(), "e");
        //
        String value = CommonUtil.fetchFirstNumOnRegexAtGroup(Y_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer year = Integer.valueOf(value.trim());
            dateExpr.setYear(year);
        }
        value = CommonUtil.fetchFirstNumOnRegexAtGroup(M_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer month = Integer.valueOf(value.trim());
            dateExpr.setMonth(month);
        }

        value = CommonUtil.fetchFirstNumOnRegexAtGroup(D_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer day = Integer.valueOf(value.trim());
            dateExpr.setDay(day);
        }
        value = CommonUtil.fetchFirstNumOnRegexAtGroup(E_NUM_REGEX, exprssionText, 1);
        if (value != null) {
            Integer dow = Integer.valueOf(value.trim());
            dateExpr.setDayOfWeek(dow);
        }
        return dateExpr;
    }

    @Override
    public void validate(DateExpr dateExpr) throws HandlableException {
        List<String> errMsgs = new ArrayList<>();
        if (dateExpr.getYear() != null && (dateExpr.getYear() < 0 || dateExpr.getYear() > 10000)) {
            errMsgs.add("Y: year out of scope\n");
        }
        if (dateExpr.getMonth() != null && (dateExpr.getMonth() < 0 || dateExpr.getMonth() > 12)) {
            errMsgs.add("M: Month of year out of scope, it should be 0 (every month), and (January)  to 12 (December) \n");
        }
        if (dateExpr.getDay() != null && (dateExpr.getDay() < 1 || dateExpr.getDay() > 31)) {
            errMsgs.add("M: Day of Month out of scope, it should be 0 to 31 (December) \n");
        }
        if (dateExpr.getDayOfWeek() != null && (dateExpr.getDayOfWeek() < 1 || dateExpr.getDayOfWeek() > 7)) {
            errMsgs.add("E: Day of week out of scope, it should be 1 (Monday)  to 7 (Sunday) \n");
        }
        if (errMsgs.size() > 0) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
        }
    }

    @Override
    public String parseParams(DateExpr dateExpr, String expressions) {
        if (dateExpr == null) {
            dateExpr = new DateExpr();
        }
        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                dateExpr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public String toExpr(DateExpr dateExpr) {
        StringBuilder expr = new StringBuilder();
        if (dateExpr.getYear() != null) {
            expr.append("Y");
            expr.append(dateExpr.getYear());
        }
        if (dateExpr.getMonth() != null) {
            expr.append("M");
            expr.append(dateExpr.getMonth());
        }
        if (dateExpr.getDay() != null) {
            expr.append("D");
            expr.append(dateExpr.getDay());
        }
        if (dateExpr.getDayOfWeek() != null) {
            expr.append("E");
            expr.append(dateExpr.getDayOfWeek());
        }

        //expr.push_str(dateExpr.value.trim());
        expr.append(CommonUtil.toParamsExpr(dateExpr.getParams()));
        return expr.toString();
    }

    /**
     * @param dateExpr
     * @param date
     * @return as simple evaluable expression
     */
    @Override
    public String calculateExpr(DateExpr dateExpr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(dateExpr.getParams(), date);
        for (LocalDate theDate : dates) {
            boolean isEq = true;
            if (dateExpr.getYear() != null && dateExpr.getYear() != theDate.getYear() ||
                    dateExpr.getMonth() != null && dateExpr.getMonth() != theDate.getMonthValue() ||
                    dateExpr.getDay() != null && dateExpr.getDay() != theDate.getDayOfMonth() ||
                    dateExpr.getDayOfWeek() != null && dateExpr.getDayOfWeek() != theDate.getDayOfWeek().getValue()) {
                isEq = false;
            }
            if (isEq) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);
    }

}
