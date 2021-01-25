/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.LastDayOfXMonthExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.util.List;
import java.util.regex.Pattern;

class LastDayOfXMonthExprTranslator implements Translator<LastDayOfXMonthExpr> {
    public static final Pattern BASED_WORDS = Pattern.compile("^((THE){0,1}[\\s]{0,}(LAST){1}[\\s]{1,}(DAY){1}[\\s]{1,}(IN|OF|AT){1})");
    public static final Pattern EVERY_MONTH_WORDS = Pattern.compile("^(EVERY|ALL|EACH|THE){0,1}[\\s]{0,}(MONTH)[S]{0,1}$");
    public static final List<String> MONTH_NAMES = MonthWeekNumUtil.getMonthNames();

    @Override
    public String exprToLanguage(LastDayOfXMonthExpr expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("The last day of ");
        if (expr.getMonth() != null) {
            sb.append(MonthWeekNumUtil.num2Month(expr.getMonth()));
        } else {
            sb.append("every month");
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
        language = language.toUpperCase();
        if (BASED_WORDS.matcher(language).find()) {
            String leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS, language, 1);
            language = language.substring(leading.length());
            return language != null && EVERY_MONTH_WORDS.matcher(language.trim()).find() || MonthWeekNumUtil.isMonth(language);
        }
        return false;
    }

    @Override
    public String languageToFormula(String language) {
        if (language == null || language.trim().isEmpty()) {
            return null;
        }
        language = language.toUpperCase();
        if (BASED_WORDS.matcher(language).find()) {
            String leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS, language, 1);
            language = language.substring(leading.length());
            if (language != null && EVERY_MONTH_WORDS.matcher(language.trim()).find() || MonthWeekNumUtil.isMonth(language)) {
                String result = "LDM";
                if (MonthWeekNumUtil.isMonth(language)) {
                    result += MonthWeekNumUtil.month2Num(language.trim());
                }
                return result;
            }
        }
        throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", language);
    }
}
