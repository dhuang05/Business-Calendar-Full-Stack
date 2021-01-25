/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.AddOnFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.GoodFridayFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.util.regex.Pattern;

class AddOnFuncExprTranslator implements Translator<AddOnFuncExpr> {

    @Override
    public String exprToLanguage(AddOnFuncExpr expr) {
        StringBuilder sb = new StringBuilder(expr.getExpression());
        if (expr.getParams() != null && expr.getParams().size() > 0) {
            sb.append(CommonUtil.toParamsExpr(expr.getParams()));
        }
        return sb.toString().trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        return AddOnFuncExprInterpretor.isMyLanguage(language.toUpperCase());
    }

    @Override
    public String languageToFormula(String language) {
        if (language == null || language.trim().isEmpty()) {
            return null;
        }

        if (isLanguageMyElement(language.toUpperCase())) {
            return language;
        } else {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "rule element error", "X: Unknown " + language);
        }
    }
}
