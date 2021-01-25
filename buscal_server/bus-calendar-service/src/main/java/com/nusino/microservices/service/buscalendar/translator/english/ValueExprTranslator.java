/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.service.buscalendar.expr.model.standard.ValueExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

class ValueExprTranslator implements Translator<ValueExpr> {
    public static final Pattern NUMBER_REGEX = Pattern.compile("^([\\d]{1,}[.]{0,1}[\\d]{0,})$");
    public static final Pattern BOOLEAN_REGEX = Pattern.compile("^([Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee])$");

    @Override
    public String exprToLanguage(ValueExpr expr) {
        return expr.getValue();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || StringUtils.isEmpty(language.trim())) {
            return false;
        }
        language = language.trim();
        return NUMBER_REGEX.matcher(language).find() || BOOLEAN_REGEX.matcher(language).find();
    }

    @Override
    public String languageToFormula(String language) {
        if (!isLanguageMyElement(language)) {
            return "";
        }
        language = language.trim();
        return language.toLowerCase();
    }
}
