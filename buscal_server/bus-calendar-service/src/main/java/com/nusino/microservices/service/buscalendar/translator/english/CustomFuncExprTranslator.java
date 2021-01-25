
/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.service.buscalendar.expr.model.standard.func.CustomFuncExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

class CustomFuncExprTranslator implements Translator<CustomFuncExpr> {
    public static final Pattern FUNCTION_HEADER_EXPR = Pattern.compile("^(function\\s*([A-z0-9]+)?\\s*\\((?:[^)(]+|\\((?:[^)(]+|\\([^)(]*\\))*\\))*\\)\\s*\\{)");

    @Override
    public String exprToLanguage(CustomFuncExpr expr) {
        StringBuilder sb = new StringBuilder(expr.getExpression());
        if (expr.getParams() != null && expr.getParams().size() > 0) {
            sb.append(CommonUtil.toParamsExpr(expr.getParams()));
        }
        return "\n" + sb.toString().trim() + "\n";
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || StringUtils.isEmpty(language.trim())) {
            return false;
        }
        language = language.trim();
        return FUNCTION_HEADER_EXPR.matcher(language).find();
    }

    @Override
    public String languageToFormula(String language) {
        if (language == null || StringUtils.isEmpty(language.trim())) {
            return null;
        }
        language = language.trim();
        //language = language.replaceAll("[\\r]{1}", "\n");
        Integer count = null;
        StringBuilder sb = new StringBuilder();
        for (char ch : language.toCharArray()) {
            if (count != null && count == 0) {
                break;
            }
            if (ch == '{') {
                if (count == null) {
                    count = 0;
                }
                count++;
            } else if (ch == '}') {
                if (count == null) {
                    count = 0;
                }
                count--;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
