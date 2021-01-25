/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.DateExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class DateExprTranslator implements Translator<DateExpr> {
    public static final Pattern YYYY_EXPR = Pattern.compile("^[0-9]{4}$");
    public static final Pattern DD_EXPR = Pattern.compile("^[0-9]{1,2}(ST|ND|RD|TH){0,1}$");
    public static final Pattern SEPARATOR = Pattern.compile("[\\s|,|/]{1,}");

    @Override
    public String exprToLanguage(DateExpr expr) {
        StringBuilder sb = new StringBuilder();
        if (expr.getYear() != null) {
            sb.append(expr.getYear());
        }
        if (expr.getMonth() != null) {
            sb.append(" ").append(MonthWeekNumUtil.num2Month(expr.getMonth()));
        }
        if (expr.getDay() != null) {
            sb.append(" ").append(MonthWeekNumUtil.num2Order(expr.getDay()));
        }

        if (expr.getDayOfWeek() != null) {
            sb.append(" ").append(MonthWeekNumUtil.num2DayOfWeek(expr.getDayOfWeek()));
        }

        if (expr.getParams() != null && expr.getParams().size() > 0) {
            sb.append(CommonUtil.toParamsExpr(expr.getParams()));
        }
        return sb.toString().trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        //
        language = language.trim().toUpperCase();
        List<String> parts = CommonUtil.toNoEmpty(SEPARATOR.split(language));
        boolean isMyLanguagePart = parts != null && parts.size() > 0;
        for (String part : parts) {
            part = part.trim();
            if (YYYY_EXPR.matcher(part).find() || DD_EXPR.matcher(part).find() || MonthWeekNumUtil.isMonth(part) || MonthWeekNumUtil.isDayOfWeek(part)) {
                continue;
            } else {
                return false;
            }
        }
        return isMyLanguagePart;
    }


    @Override
    public String languageToFormula(String language) {
        if (language == null || language.trim().isEmpty()) {
            return null;
        }
        //
        language = language.trim().toUpperCase();
        List<String> parts = CommonUtil.toNoEmpty(SEPARATOR.split(language));
        StringBuilder sb = new StringBuilder();
        List<String> errMsgs = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            if (YYYY_EXPR.matcher(part).find()) {
                sb.append("Y").append(part.trim());
            } else if (DD_EXPR.matcher(part).find()) {
                Integer num = CommonUtil.fetchFirstInt(part);
                if (num > 31) {
                    throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "Data error", "Day of month should not more then 31, but it is " + num);
                }
                sb.append("D").append(num);
            } else if (MonthWeekNumUtil.isMonth(part)) {
                sb.append(MonthWeekNumUtil.month2Formula(part));
            } else if (MonthWeekNumUtil.isDayOfWeek(part)) {
                sb.append(MonthWeekNumUtil.dayOfWeek2Formula(part));
            } else {
                errMsgs.add("X: Unknown '" + part + "'");
                throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", errMsgs.toArray(new String[errMsgs.size()]));
            }
        }
        return sb.toString();
    }

}
