/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr;

import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.CustomFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.vo.buscalendar.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DayRuleElementAnalyzer {
    //public static final String[] LOGIC_SET = new String[]{"&&", "||", "==", "!=", ">=", "<=", "&", "|", "!", ">", "<", "+", "-", "*", "/", "%", "(", ")"};
    public static final String[] LOGIC_SET = new String[]{"&&", "||", "==", "!=", ">=", "<=", "&", "|", "!", ">", "<", "(", ")"};
    public static final Pattern YMD_VALUE_REGEX = Pattern.compile("^([\\d|,|.|\\-|\\s]{1,}[;]{1,})");
    public static final Pattern FUNCTION_HEADER_EXPR = Pattern.compile("^function\\s*([A-z0-9]+)?\\s*\\((?:[^)(]+|\\((?:[^)(]+|\\([^)(]*\\))*\\))*\\)\\s*\\{");
    public final static Pattern PARAMS_REGEX = Pattern.compile("^([(]{1}[\\+|\\-|\\d|,|\\s]{1,}[)]{1})");
    private final static Pattern S_REGEX = Pattern.compile("^([\\s]{1,})");

    // ORDER is matter
    private final static Pattern[] KEY_PATTERNS = new Pattern[]{YMD_VALUE_REGEX, FUNCTION_HEADER_EXPR, PARAMS_REGEX};

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
            for (Pattern pattern : KEY_PATTERNS) {
                if (pattern.matcher(currentContent).find()) {
                    if (!element.hasContent()) {
                        elements.add(element);
                        element = new Element();
                    }
                    String expr = CommonUtil.fetchRegexAtGroup(pattern, currentContent, 1);
                    if (expr == null || expr.trim().isEmpty()) {
                        break OUTER;
                    }
                    //
                    if (pattern == YMD_VALUE_REGEX) {
                        element.setType(Element.ElementType.DATE_RANGE);
                    } else if (pattern == FUNCTION_HEADER_EXPR) {
                        expr = CustomFuncExprInterpretor.fetchFunction(currentContent);
                        element.setType(Element.ElementType.FUNC);
                    } else if (pattern == PARAMS_REGEX) {
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
            for (String logic : LOGIC_SET) {
                if (currentContent.startsWith(logic)) {
                    if (!element.hasContent()) {
                        elements.add(element);
                        element = new Element();
                    }
                    element.setType(Element.ElementType.LOGIC);
                    String expr = logic;
                    element.append(expr);
                    i += expr.length() - 1;
                    elements.add(element);
                    element = new Element();
                    continue OUTER;
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
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element.getText() == null || element.getText().trim().isEmpty()) {
                removables.add(element);
            } else {
                element.setText(element.getText().trim());
            }
        }
        elements.removeAll(removables);
        return elements;
    }

}
