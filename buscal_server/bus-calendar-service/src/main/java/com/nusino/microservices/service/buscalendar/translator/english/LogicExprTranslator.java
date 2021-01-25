/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.service.buscalendar.expr.model.standard.LogicExpr;
import com.nusino.microservices.service.buscalendar.translator.Translator;

import java.util.HashMap;
import java.util.Map;

class LogicExprTranslator implements Translator<LogicExpr> {
    //public static final String[] LOGIC_SYMBOLS = new String[]{"&&", "||", "==", "!=", ">=", "<=", "&", "|", "!", ">", "<", "+", "-", "*", "/", "%", "(", ")"};
    public static final String[] LOGIC_SYMBOLS = new String[]{"&&", "||", "==", "!=", ">=", "<=", "&", "|", "!", ">", "<", "(", ")"};
    public static final String[] LOGIC_WORDS = new String[]{"NOT", "OR", "AND"};
    public static Map<String, String> LODIC_CONVERTING_MAP = new HashMap<>();

    static {
        LODIC_CONVERTING_MAP.put("AND", "&&");
        LODIC_CONVERTING_MAP.put("OR", "||");
        LODIC_CONVERTING_MAP.put("NOT", "!");
    }


    @Override
    public String exprToLanguage(LogicExpr expr) {
        String result = expr.getExpression();
        result = result.replaceAll("[&]{1,}", " and ");
        result = result.replaceAll("[\\|]{1,}", " or ");
        result = result.replaceAll("[!]{1,}", " not ");
        return result.trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        language = language.trim().toUpperCase();
        for (String logic : LOGIC_SYMBOLS) {
            if (language.equals(logic)) {
                return true;
            }
        }
        for (String logic : LOGIC_WORDS) {
            if (language.equals(logic)) {

                return true;
            }
        }
        return false;
    }

    @Override
    public String languageToFormula(String language) {
        if (!isLanguageMyElement(language)) {
            return null;
        }
        language = language.trim().toUpperCase();
        String converted = LODIC_CONVERTING_MAP.get(language);
        if (converted != null) {
            return converted;
        } else {
            return language;
        }
    }
}
