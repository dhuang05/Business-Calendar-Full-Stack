/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.translator.english.EnglishDayRuleTranslator;
import com.nusino.microservices.service.buscalendar.util.KeySmith;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class TranslationEngine {
    private final static Map<String, Translator> LANGUAGE_TRANSLATORS = new HashMap<>();

    static {
        LANGUAGE_TRANSLATORS.put(KeySmith.makeKey(Locale.ENGLISH.toString()), new EnglishDayRuleTranslator());
    }

    public String toLanguage(Expr expr, String language) {
        if (language == null) {
            language = Locale.ENGLISH.toString();
        }
        return LANGUAGE_TRANSLATORS.get(KeySmith.makeKey(language)).exprToLanguage(expr);
    }

    public String toFormula(String text, String language) throws FeedbackableException {
        if (language == null) {
            language = Locale.ENGLISH.toString();
        }
        return LANGUAGE_TRANSLATORS.get(KeySmith.makeKey(language)).languageToFormula(text);
    }
}
