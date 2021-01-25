/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.YmdValueExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.util.List;
import java.util.regex.Pattern;

class YmdValueExprTranslator implements Translator<YmdValueExpr> {
    public static final Pattern LEADING_REGEX = Pattern.compile("^((FROM|BETWEEN)|((IN\\s+){0,1})(THE\\s+){0,1}(RANGE)\\s+(OF\\s+){0,1})");
    public static final Pattern MIDDLE_REGEX = Pattern.compile("[\\s]{1}(AND|TO)[\\s]{1}");
    //
    public static final Pattern YYYY_EXPR = Pattern.compile("^[0-9]{4}$");
    public static final Pattern DD_EXPR = Pattern.compile("^[0-9]{1,2}(ST|ND|RD|TH){0,1}$");
    public static final Pattern ENDING_REGEX = Pattern.compile("([;|:|\\r|\\n]{1,})");
    //
    public static final Pattern YMD_VALUE_REGEX1 = Pattern.compile("^((FROM\\s+)[a-zA-Z0-9|,|/|\\s]{5,}\\s+(TO)\\s+[a-zA-Z0-9|,|/|\\s]{5,}" + ENDING_REGEX.pattern() + ")");
    public static final Pattern YMD_VALUE_REGEX2 = Pattern.compile("^((BETWEEN\\s+)[a-zA-Z0-9|,|/|\\s]{5,}\\s+(AND){1,}\\s+[a-zA-Z0-9|,|/|\\s]{5,}" + ENDING_REGEX.pattern() + ")");
    public static final Pattern YMD_VALUE_REGEX3 = Pattern.compile("^((IN\\s+){0,1}(THE\\s+){0,1}(RANGE)\\s+(OF\\s+){0,1}[a-zA-Z0-9|,|/|\\s]{5,}[\\s]{1,}(AND)\\s+[a-zA-Z0-9|,|/|\\s]{5,}" + ENDING_REGEX.pattern() + ")");
    //
    public static final Pattern JUST_FROM = Pattern.compile("^((FROM)[\\s]{1,}[a-zA-Z0-9|,|/|\\s]{5,}" + ENDING_REGEX.pattern() + ")");
    //
    private final static Pattern[] KEY_PATTERNS = new Pattern[]{YmdValueExprTranslator.YMD_VALUE_REGEX1, YmdValueExprTranslator.YMD_VALUE_REGEX2, YmdValueExprTranslator.YMD_VALUE_REGEX3};
    public static final Pattern SEPARATOR = Pattern.compile("[\\s|,|/]{1,}");

    @Override
    public String exprToLanguage(YmdValueExpr expr) {
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
        return sb.toString().trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        language = language.trim().toUpperCase();
        for (Pattern pattern : KEY_PATTERNS) {
            if (pattern.matcher(language).find()) {
                return true;
            }
        }
        return JUST_FROM.matcher(language).find();
    }

    @Override
    public String languageToFormula(String language) {
        if (language == null || language.trim().isEmpty()) {
            return null;
        }
        language = language.trim().toUpperCase();
        if (isLanguageMyElement(language)) {
            String leading = CommonUtil.fetchRegexAtMaxLenGroup(LEADING_REGEX, language);
            language = language.substring(leading.length());
            if (language != null && !language.trim().isEmpty()) {
                language = language.trim();
                String ending = CommonUtil.fetchRegexAtGroup(ENDING_REGEX, language, 1);
                if (ending != null) {
                    language = language.substring(0, language.length() - ending.length());
                }
                List<String> parts = CommonUtil.toNoEmpty(MIDDLE_REGEX.split(language));
                if (parts.size() > 0) {
                    String ret = toYmd(parts.get(0));
                    if (parts.size() > 1) {
                        ret += "," + toYmd(parts.get(1));
                    }
                    return ret + ";";
                }
            }
        }
        throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", language);
    }

    public String toYmd(String text) {
        List<String> parts = CommonUtil.toNoEmpty(SEPARATOR.split(text));
        String y = null;
        String d = null;
        Integer m = null;
        for (String part : parts) {
            part = part.trim();
            if (YYYY_EXPR.matcher(part).find()) {
                y = part.trim();
            } else if (DD_EXPR.matcher(part).find()) {
                d = CommonUtil.fetchDigits(part).get(0);
            } else if (MonthWeekNumUtil.isMonth(part)) {
                m = MonthWeekNumUtil.month2Num(part);
            } else {
                throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", "X: Unknown '" + part + "' in " + text);
            }
        }
        if (d == null || m == null) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", "X: The date range should contains at least Month and day '");
        }
        String ret = m + "-" + d;
        if (y != null) {
            ret = y + "-" + ret;
        }

        return ret;
    }

}
