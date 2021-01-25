/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;

import java.util.regex.Pattern;

public interface Translator<T extends Expr> {
    String SPACE = " ";
    Pattern S_REGEX = Pattern.compile("\\s+");
    Pattern PARAMS_REGEX = Pattern.compile("^([(]{1}[\\+|\\-|\\d|,|\\s]{1,}[)]{1})");
    Pattern AZ_09 = Pattern.compile("^[a-zA-Z0-9]{1,}");

    String exprToLanguage(T expr);

    String languageToFormula(String language) throws FeedbackableException;

    boolean isLanguageMyElement(String language);

}
