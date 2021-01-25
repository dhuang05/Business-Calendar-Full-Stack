/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.XDayOfWeekXInYearExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

class XDayOfWeekXInYearExprTranslator implements Translator<XDayOfWeekXInYearExpr> {
    public static final Pattern BASED_WORDS = Pattern.compile("^((THE){0,1}[\\s]{0,}[0-9]{1,2}(ST|ND|RD|TH){0,1})");
    public static final Pattern YEAR_PART = Pattern.compile("^((IN|OF|AT){0,1}[\\s]{0,}(EVERY|ALL|EACH|THE){0,1}[\\s]{0,}(YEAR){1}[S]{0,1}$)");
    public static final Pattern DOW = Pattern.compile("([0-9]{1,2})");

    @Override
    public String exprToLanguage(XDayOfWeekXInYearExpr expr) {
        StringBuilder sb = new StringBuilder("The");
        sb.append(" ").append(MonthWeekNumUtil.num2Order(expr.getXth()));
        sb.append(" ").append(MonthWeekNumUtil.num2DayOfWeek(expr.getDayOfWeek()));
        sb.append(" of the year");
        if (expr.getParams() != null && expr.getParams().size() > 0) {
            sb.append(CommonUtil.toParamsExpr(expr.getParams()));
        }
        return sb.toString().trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || StringUtils.isEmpty(language.trim())) {
            return false;
        }
        language = language.trim().toUpperCase();
        if (BASED_WORDS.matcher(language).find()) {
            String leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS, language, 1);
            language = language.substring(leading.length());
            if (language != null && !language.trim().isEmpty()) {
                language = language.trim();
                List<String> parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                if (parts.size() > 0) {
                    if (MonthWeekNumUtil.isDayOfWeek(parts.get(0))) {
                        language = language.substring(parts.get(0).length());
                        if (language != null && !language.trim().isEmpty()) {
                            language = language.trim();
                            return YEAR_PART.matcher(language).find();
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String languageToFormula(String language) {
        if (language == null || StringUtils.isEmpty(language.trim())) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        language = language.trim().toUpperCase();
        if (BASED_WORDS.matcher(language).find()) {
            String leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS, language, 1);
            Integer num = Integer.valueOf(CommonUtil.fetchFirstNumOnRegexAtGroup(DOW, leading, 1));
            if (num > 55) {
                throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "Week number of month cannot more then 55 but it is " + num, language);
            }
            sb.append("W").append(num);
            language = language.substring(leading.length());
            if (language != null && !language.trim().isEmpty()) {
                language = language.trim();
                List<String> parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                if (parts.size() > 0) {
                    if (MonthWeekNumUtil.isDayOfWeek(parts.get(0))) {
                        sb.append(MonthWeekNumUtil.dayOfWeek2Formula(parts.get(0)));
                        language = language.substring(parts.get(0).length());
                        if (language != null && !language.trim().isEmpty()) {
                            language = language.trim();
                            if (YEAR_PART.matcher(language).find()) {
                                sb.append("Y");
                                return sb.toString();
                            }
                        }
                    }
                }
            }
        }
        throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", language);
    }
}
