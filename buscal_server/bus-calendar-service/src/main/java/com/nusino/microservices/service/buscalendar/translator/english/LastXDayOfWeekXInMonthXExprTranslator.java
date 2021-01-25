/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.LastXDayOfWeekXInMonthXExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

class LastXDayOfWeekXInMonthXExprTranslator implements Translator<LastXDayOfWeekXInMonthXExpr> {
    public static final Pattern BASED_WORDS_1 = Pattern.compile("^((THE[\\s]{1,}){0,1}(LAST[\\s]{1,}){1}[0-9]{1,2}(ST|ND|RD|TH){0,1}[\\s]{1,})");
    public static final Pattern BASED_WORDS_2 = Pattern.compile("^((THE[\\s]{1,}){0,1}[0-9]{1,2}(ST|ND|RD|TH){0,1}[\\s]{1,}(LAST[\\s]{1,}){1})");
    public static final Pattern BASED_WORDS_3 = Pattern.compile("^((THE[\\s]{1,}){0,1}(LAST[\\s]{1,}){1})");
    public static final Pattern DOW = Pattern.compile("([0-9]{1,2})");
    public static final Pattern IN_OF_OTHER = Pattern.compile("^((IN|OF|AT){0,1}[\\s]{0,}(EVERY|ALL|EACH|THE){0,1})");
    public static final Pattern EVERY_MONTH_WORDS = Pattern.compile("^(MONTH)[S]{0,1}$");

    @Override
    public String exprToLanguage(LastXDayOfWeekXInMonthXExpr expr) {
        StringBuilder sb = new StringBuilder();

        if (expr.getXth() == 1) {
            sb.append("The last");
        } else {
            sb.append("The ").append(MonthWeekNumUtil.num2Order(expr.getXth())).append(" last");
        }

        sb.append(" ").append(MonthWeekNumUtil.num2DayOfWeek(expr.getDayOfWeek()));
        if (expr.getMonth() != null) {
            sb.append(" of ").append(MonthWeekNumUtil.num2Month(expr.getMonth()));
        } else {
            sb.append(" of every month");
        }
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
        if (BASED_WORDS_1.matcher(language).find() || BASED_WORDS_2.matcher(language).find() || BASED_WORDS_3.matcher(language).find()) {

            String leading = null;
            if (BASED_WORDS_1.matcher(language).find()) {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_1, language, 1);
            } else if (BASED_WORDS_2.matcher(language).find()) {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_2, language, 1);
            } else {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_3, language, 1);
            }


            language = language.substring(leading.length());
            //find day of week
            if (language != null && !language.trim().isEmpty()) {
                language = language.trim();
                List<String> parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                if (parts.size() > 0) {
                    if (MonthWeekNumUtil.isDayOfWeek(parts.get(0))) {
                        //find IN_OF
                        language = language.substring(parts.get(0).length());
                        if (language != null && !language.trim().isEmpty()) {
                            language = language.trim();
                            if (IN_OF_OTHER.matcher(language).find()) {
                                //find month or every month
                                leading = CommonUtil.fetchRegexAtGroup(IN_OF_OTHER, language, 1);
                                language = language.substring(leading.length());
                                parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                                if (parts != null && parts.size() > 0) {
                                    if (MonthWeekNumUtil.isMonth(parts.get(0))) {
                                        return true;
                                    } else if (language != null && !language.trim().isEmpty()) {
                                        return EVERY_MONTH_WORDS.matcher(language.trim()).find();
                                    }
                                }
                            }
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
        if (BASED_WORDS_1.matcher(language).find() || BASED_WORDS_2.matcher(language).find() || BASED_WORDS_3.matcher(language).find()) {
            String leading = null;
            int regNum = 0;
            if (BASED_WORDS_1.matcher(language).find()) {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_1, language, 1);
                regNum = 1;
            } else if (BASED_WORDS_2.matcher(language).find()) {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_2, language, 1);
                regNum = 2;
            } else {
                leading = CommonUtil.fetchRegexAtGroup(BASED_WORDS_3, language, 1);
                regNum = 3;
            }
            language = language.substring(leading.length());
            Integer num = 1;
            if (regNum == 1 || regNum == 2) {
                num = Integer.valueOf(CommonUtil.fetchFirstNumOnRegexAtGroup(DOW, leading, 1));
                if (num > 5) {
                    throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "Week number of month cannot more then 5 but it is " + num, language);
                }
            }

            sb.append("LW").append(num);
            //find day of week
            if (language != null && !language.trim().isEmpty()) {
                language = language.trim();
                List<String> parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                if (parts.size() > 0) {
                    if (MonthWeekNumUtil.isDayOfWeek(parts.get(0))) {
                        sb.append(MonthWeekNumUtil.dayOfWeek2Formula(parts.get(0)));
                        //find IN_OF
                        language = language.substring(parts.get(0).length());
                        if (language != null && !language.trim().isEmpty()) {
                            language = language.trim();
                            if (IN_OF_OTHER.matcher(language).find()) {
                                //find month or every month
                                leading = CommonUtil.fetchRegexAtGroup(IN_OF_OTHER, language, 1);
                                language = language.substring(leading.length());
                                parts = CommonUtil.toNoEmpty(S_REGEX.split(language));
                                if (parts != null && parts.size() > 0) {
                                    if (MonthWeekNumUtil.isMonth(parts.get(0))) {
                                        sb.append(MonthWeekNumUtil.month2Formula(parts.get(0)));
                                        return sb.toString();
                                    } else if (language != null && !language.trim().isEmpty()) {
                                        sb.append("M");
                                        return sb.toString();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", language);
    }
}
