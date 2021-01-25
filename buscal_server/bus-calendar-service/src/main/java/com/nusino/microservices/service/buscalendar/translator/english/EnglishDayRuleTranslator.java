/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.*;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.AddOnFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.CustomFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.GoodFridayFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.translator.Translator;
import com.nusino.microservices.vo.buscalendar.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnglishDayRuleTranslator implements Translator<DayRuleExpr> {
    public static final Map<Class, Translator> ALL_TRANSLATORS = new HashMap<>();
    public static final List<Translator> EXPR_TRANSLATORS = new ArrayList<>();
    public static final YmdValueExprTranslator ymdValueExprTranslator = new YmdValueExprTranslator();
    public static final LogicExprTranslator logicTranslator = new LogicExprTranslator();

    public static final Map<String, String> POSSIBLE_REPLACEMENTS = new HashMap<>();


    static {
        ALL_TRANSLATORS.put(YmdValueExpr.class, ymdValueExprTranslator);
        ALL_TRANSLATORS.put(DateExpr.class, new DateExprTranslator());
        ALL_TRANSLATORS.put(LastDayOfXMonthExpr.class, new LastDayOfXMonthExprTranslator());

        ALL_TRANSLATORS.put(LastXDayOfWeekXInMonthXExpr.class, new LastXDayOfWeekXInMonthXExprTranslator());
        ALL_TRANSLATORS.put(XDayOfWeekXInMonthXExpr.class, new XDayOfWeekXInMonthXExprTranslator());
        ALL_TRANSLATORS.put(XDayOfWeekXInYearExpr.class, new XDayOfWeekXInYearExprTranslator());
        ALL_TRANSLATORS.put(ValueExpr.class, new ValueExprTranslator());
        ALL_TRANSLATORS.put(LogicExpr.class, new LogicExprTranslator());
        ALL_TRANSLATORS.put(CustomFuncExpr.class, new CustomFuncExprTranslator());
        ALL_TRANSLATORS.put(AddOnFuncExpr.class, new AddOnFuncExprTranslator());

        //
        EXPR_TRANSLATORS.add(new ValueExprTranslator());
        EXPR_TRANSLATORS.add(new DateExprTranslator());
        EXPR_TRANSLATORS.add(new LastDayOfXMonthExprTranslator());
        EXPR_TRANSLATORS.add(new LastXDayOfWeekXInMonthXExprTranslator());
        EXPR_TRANSLATORS.add(new XDayOfWeekXInMonthXExprTranslator());
        EXPR_TRANSLATORS.add(new XDayOfWeekXInYearExprTranslator());
        EXPR_TRANSLATORS.add(new AddOnFuncExprTranslator());
        EXPR_TRANSLATORS.add(new CustomFuncExprTranslator());
        //

        POSSIBLE_REPLACEMENTS.put("FIRST ", "1st");
        POSSIBLE_REPLACEMENTS.put("SECOND ", "2nd");
        POSSIBLE_REPLACEMENTS.put("THIRD ", "3rd");
        POSSIBLE_REPLACEMENTS.put("FOURTH ", "4th");
        POSSIBLE_REPLACEMENTS.put("FIFTH ", "5th");
        POSSIBLE_REPLACEMENTS.put("SIXTH ", "6th");

    }

    @Override
    public String exprToLanguage(DayRuleExpr dayRuleExpr) {
        if (dayRuleExpr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean hasRange = false;
        if (dayRuleExpr.getFrom() != null) {
            sb.append(" From ").append(ALL_TRANSLATORS.get(YmdValueExpr.class).exprToLanguage(dayRuleExpr.getFrom()));
            hasRange = true;
        }
        if (dayRuleExpr.getTo() != null) {
            sb.append(" To ").append(ALL_TRANSLATORS.get(YmdValueExpr.class).exprToLanguage(dayRuleExpr.getTo()));
            hasRange = true;
        }
        if (hasRange) {
            sb.append(";\n");
        }

        for (Expr expr : dayRuleExpr.getExprs()) {
            String ret = ALL_TRANSLATORS.get(expr.getClass()).exprToLanguage(expr);
            sb.append(" ").append(ret);
        }
        return sb.toString().trim();
    }

    @Override
    public boolean isLanguageMyElement(String language) {
        return false;
    }

    @Override
    public String languageToFormula(String language) throws FeedbackableException {
        AtomicBoolean hasError = new AtomicBoolean(false);
        List<Element> elements = DayRuleLanguageElementAnalyzer.analyze(language);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            boolean parsed = false;
            Element element = elements.get(i);
            if (element.getType() == Element.ElementType.DATE_RANGE) {
                toFormula(ymdValueExprTranslator, element, sb, hasError);
                parsed = true;
            } else if (element.getType() == Element.ElementType.EXPR || element.getType() == Element.ElementType.FUNC) {
                for (Translator translator : EXPR_TRANSLATORS) {
                    if (translator.isLanguageMyElement(element.getText())) {
                        toFormula(translator, element, sb, hasError);
                        parsed = true;
                        break;
                    }
                }
            } else if (element.getType() == Element.ElementType.PARAM) {
                sb.append(element.getText());
                parsed = true;
            } else if (element.getType() == Element.ElementType.LOGIC) {
                sb.append(SPACE + logicTranslator.languageToFormula(element.getText()) + SPACE);
                parsed = true;
            }

            ///
            if (!parsed) {
                String msg = "X: unknown expression. ";
                String text = element.getText().toUpperCase().trim();
                for (String key : POSSIBLE_REPLACEMENTS.keySet()) {
                    if (text.startsWith(key)) {
                        msg += key + "->" + POSSIBLE_REPLACEMENTS.get(key) + ". ";
                    }
                }
                element.addError(msg);
                hasError.set(true);
            }
        }
        if (hasError.get()) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, elements);
        }
        return sb.toString();
    }


    private boolean toFormula(Translator translator, Element element, StringBuilder sb, AtomicBoolean hasError) {
        try {
            if (translator.isLanguageMyElement(element.getText())) {
                sb.append(translator.languageToFormula(element.getText()));
                return true;
            }
        } catch (FeedbackableException fbEx) {
            element.addError(fbEx.getMessage());
            hasError.set(true);

        } catch (RuntimeException rtEx) {
            element.addError(rtEx.getMessage());
            hasError.set(true);

        }
        return false;
    }
}
