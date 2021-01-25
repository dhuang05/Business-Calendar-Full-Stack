/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.LastDayOfXMonthExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LastDayOfXMonthExprInterpretor implements ExprInterpretor<LastDayOfXMonthExpr> {
    public static final Pattern LAST_DAY_OF_X_MONTH_UPPER_REGEX = Pattern.compile("^(LDM[\\d]{0,2})$");
    public static final Pattern NON_DIGIT_REGEX = Pattern.compile("[^\\d]{1,}");
    public static final Pattern S_REGEX = Pattern.compile("\\s+");

    public LastDayOfXMonthExprInterpretor() {
    }


    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return LAST_DAY_OF_X_MONTH_UPPER_REGEX.matcher(exprssionText.toUpperCase()).find();
    }


    @Override
    public LastDayOfXMonthExpr parse(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return null;
        }
        exprssionText = exprssionText.trim();
        LastDayOfXMonthExpr funcExpr = new LastDayOfXMonthExpr();
        funcExpr.setExpression(exprssionText);
        exprssionText = exprssionText.replaceAll(NON_DIGIT_REGEX.pattern(), SPACE);
        String[] parts = S_REGEX.split(exprssionText);
        List<String> partsClean = CommonUtil.toNoEmpty(parts);
        if (partsClean.size() > 0) {
            int month = Integer.valueOf(partsClean.get(0));
            if (month != 0) {
                funcExpr.setMonth(month);
            }
            if (month < 0 || month > 12) {
                throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", "Month of of scope, should be 0 - 12; 0 means evey month");
            }
        }
        return funcExpr;
    }

    @Override
    public String parseParams(LastDayOfXMonthExpr funcExpr, String expressions) {
        if (funcExpr == null) {
            funcExpr = new LastDayOfXMonthExpr();
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
    public void validate(LastDayOfXMonthExpr expr) throws HandlableException {
        List<String> errMsgs = new ArrayList<>();
        if (expr.getMonth() != null && (expr.getMonth() < 0 || expr.getMonth() > 12)) {
            errMsgs.add("M: Month of year out of scope, it should be 0 (every month), and (January)  to 12 (December) \n");
        }

        if (errMsgs.size() > 0) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
        }
    }

    @Override
    public String toExpr(LastDayOfXMonthExpr expr) {
        StringBuilder exprText = new StringBuilder();
        String month = "";
        if (expr.getMonth() != null) {
            month = String.valueOf(expr.getMonth());
        }
        exprText.append(String.format("LDM%s", month).trim());
        exprText.append(CommonUtil.toParamsExpr(expr.getParams()));
        return exprText.toString();
    }

    @Override
    public String calculateExpr(LastDayOfXMonthExpr expr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(expr.getParams(), date);
        for (LocalDate theDate : dates) {
            boolean isMatched = true;
            LocalDate lastDayOfMonth = fetchLastDayOfMonth(theDate.getYear(), theDate.getMonthValue());
            if (lastDayOfMonth.getYear() != theDate.getYear() || lastDayOfMonth.getMonthValue() != theDate.getMonthValue() || lastDayOfMonth.getDayOfMonth() != theDate.getDayOfMonth()) {
                isMatched = false;
            }
            if (isMatched) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);
    }

    public LocalDate fetchLastDayOfMonth(Integer year, Integer month) {
        Integer key = year * 10 + month;
        LocalDate lastDayOfMonth = null;
        if (lastDayOfMonth != null) {
            return lastDayOfMonth;
        } else {
            return LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth());
        }
    }
}
