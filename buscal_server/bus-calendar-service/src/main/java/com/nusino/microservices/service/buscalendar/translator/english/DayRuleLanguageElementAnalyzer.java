/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.CustomFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.vo.buscalendar.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

class DayRuleLanguageElementAnalyzer {
    public static final String[] LOGIC_SYMBOLS = LogicExprTranslator.LOGIC_SYMBOLS;
    public static final String[] LOGIC_WORDS = LogicExprTranslator.LOGIC_WORDS;
    //this char used in month/day, like Sept/5th and logic divided as asll nne to ensure
    private static final String SLASH = "/";
    private static final Pattern ENDS_WITH_AZ09 = Pattern.compile("([A-Za-z0-9]{1,}[\\s]{0,})");
    private static final Pattern STARTS_WITH_09 = Pattern.compile("^([\\s]{0,}[/]{1,}[\\s]{0,}[0-9]{1,})");
    // ORDER is matter
    private final static Pattern[] KEY_PATTERNS = new Pattern[]{YmdValueExprTranslator.YMD_VALUE_REGEX1, YmdValueExprTranslator.YMD_VALUE_REGEX2, YmdValueExprTranslator.YMD_VALUE_REGEX3, YmdValueExprTranslator.JUST_FROM, YmdValueExprTranslator.YMD_VALUE_REGEX3,
            CustomFuncExprTranslator.FUNCTION_HEADER_EXPR, Translator.PARAMS_REGEX};

    private final static DateExprTranslator dateExprTranslator = new DateExprTranslator();

    public static List<Element> analyze(String expression) {
        List<Element> elements = new ArrayList<>();
        if (expression == null || StringUtils.isEmpty(expression.trim())) {
            return elements;
        }
        char[] chars = expression.toCharArray();
        //start parsing

        Element element = new Element();
        OUTER:
        for (int i = 0; i < chars.length; i++) {
            String currentContent = expression.substring(i);
            String currentContentUpper = currentContent.toUpperCase();
            for (Pattern pattern : KEY_PATTERNS) {
                if (pattern.matcher(currentContentUpper).find() || pattern.matcher(currentContent).find()) {
                    if (!element.hasContent()) {
                        elements.add(element);
                        element = new Element();
                    }
                    String expr = null;
                    //
                    if (pattern == YmdValueExprTranslator.YMD_VALUE_REGEX1 || pattern == YmdValueExprTranslator.YMD_VALUE_REGEX2 ||
                            pattern == YmdValueExprTranslator.YMD_VALUE_REGEX3 || pattern == YmdValueExprTranslator.JUST_FROM) {
                        element.setType(Element.ElementType.DATE_RANGE);
                        expr = CommonUtil.fetchRegexAtGroup(pattern, currentContentUpper, 1);

                    } else if (pattern == CustomFuncExprTranslator.FUNCTION_HEADER_EXPR) {
                        expr = CustomFuncExprInterpretor.fetchFunction(currentContent);
                        element.setType(Element.ElementType.FUNC);

                    } else if (pattern == Translator.PARAMS_REGEX) {
                        expr = CommonUtil.fetchRegexAtGroup(pattern, currentContent, 1);
                        element.setType(Element.ElementType.PARAM);
                    }
                    //
                    element.append(expr);
                    i += expr.length() - 1;
                    elements.add(element);
                    element = new Element();
                    continue OUTER;
                }
            }
            for (String logic : LOGIC_SYMBOLS) {
                if (currentContentUpper.startsWith(logic) && !isDateExpr(element.getText(), currentContentUpper)) {
                    if (!element.hasContent()) {
                        elements.add(element);
                        element = new Element();
                    }
                    element.setType(Element.ElementType.LOGIC);
                    String expr = logic;
                    element.append(expr.toLowerCase());
                    i += expr.length() - 1;
                    elements.add(element);
                    element = new Element();
                    continue OUTER;
                }
            }
            for (String logic : LOGIC_WORDS) {
                if (currentContentUpper.startsWith(logic)) {
                    boolean isLogic = false;
                    if (currentContentUpper.length() == logic.length()) {
                        isLogic = true;
                    } else {
                        String rest = currentContentUpper.substring(logic.length());
                        String lastChar = "";
                        if (i > 0) {
                            lastChar = String.valueOf(chars[i - 1]);
                        }
                        if (!Translator.AZ_09.matcher(lastChar).find() && !Translator.AZ_09.matcher(rest).find()) {
                            isLogic = true;
                        }
                    }
                    if (isLogic) {
                        if (!element.hasContent()) {
                            elements.add(element);
                            element = new Element();
                        }
                        element.setType(Element.ElementType.LOGIC);
                        String expr = logic;
                        element.append(expr.toLowerCase());
                        i += expr.length() - 1;
                        elements.add(element);
                        element = new Element();
                        continue OUTER;
                    }
                }
            }
            if (i < chars.length) {
                element.append(chars[i]);
            }
        }
        if (!element.hasContent()) {
            elements.add(element);
            element = null;
        }
        // nomalized
        return normalize(elements);
    }

    private static List<Element> normalize(List<Element> elements) {
        Collection<Element> removables = new ArrayList<>();
        Element element = null;
        for (int i = 0; i < elements.size(); i++) {
            element = elements.get(i);
            if (element.getText() == null || element.getText().trim().isEmpty()) {
                removables.add(element);
            } else {
                element.setText(element.getText().trim());
            }
        }
        elements.removeAll(removables);
        return elements;
    }

    private static boolean isDateExpr(String leading, String after) {
        String az09 = CommonUtil.fetchRegexAtGroup(ENDS_WITH_AZ09, leading, 1);
        if (az09 == null || az09.trim().isEmpty()) {
            return false;
        }
        after = CommonUtil.fetchRegexAtGroup(STARTS_WITH_09, after, 1);
        if (after == null || after.trim().isEmpty()) {
            return false;
        }
        az09 = az09 + after;
        return dateExprTranslator.isLanguageMyElement(az09.trim());
    }
}
