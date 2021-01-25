/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.ExprEngine;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.YmdValueExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.YmdValueExprInterpretor;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.Element;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class DayRuleExprInterpretor implements ExprInterpretor<DayRuleExpr> {
    public static final Pattern YMD_VALUE_REGEX = Pattern.compile("^([\\d|,|.|\\-|\\s]{1,}[;]{1,})");
    private static final Pattern YMD_SEP_REGEX = Pattern.compile("[,|;]{1}");
    private static final Pattern S_REGEX = Pattern.compile("\\s+");
    private static final Pattern FUNC_REGEX = Pattern.compile("^([@]{1}[a-zA-Z0-9]{1,})");
    private static final Pattern UPPER_FUNC_REGEX = Pattern.compile("^([@]{1}[A-Z0-9]{1,})");

    private final boolean isAdminMode;
    private final ExprEngine exprEngine;

    public DayRuleExprInterpretor(ExprEngine exprEngine, boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        this.exprEngine = exprEngine;
    }

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return true;
    }

    @Override
    public String parseParams(DayRuleExpr expr, String expressions) {
        return expressions;
    }


    @Override
    public DayRuleExpr parse(String expressions) {
        DayRuleExpr dayRuleExpr = new DayRuleExpr();
        List<Element> elements = DayRuleElementAnalyzer.analyze(expressions);
        AtomicBoolean hasError = new AtomicBoolean(false);
        for (int i = 0; i < elements.size(); i++) {
            boolean parsed = false;
            Element element = elements.get(i);
            if (element.getType() == Element.ElementType.DATE_RANGE) {
                addDateRangeExprs(dayRuleExpr, element, hasError);
                parsed = true;
            } else if (element.getType() == Element.ElementType.EXPR || element.getType() == Element.ElementType.FUNC) {
                for (ExprInterpretor exprInterpreter : exprEngine.getAllParameterizedExprInterpretors()) {
                    if (exprInterpreter.isMyExpr(element.getText())) {
                        Expr expr = toParameterizableExpr(exprInterpreter, element, hasError);
                        dayRuleExpr.getExprs().add(expr);
                        if (i + 1 < elements.size() && elements.get(i + 1).getType() == Element.ElementType.PARAM) {
                            element = elements.get(++i);
                            exprInterpreter.parseParams(expr, element.getText());
                        }
                        parsed = true;
                    }
                }
            } else if (element.getType() == Element.ElementType.LOGIC) {
                Expr expr = toParameterizableExpr(exprEngine.logicExprInterpretor, element, hasError);
                dayRuleExpr.getExprs().add(expr);
                parsed = true;
            }
            if (!parsed) {
                element.setError("X: unknown expression");
                hasError.set(true);
            }
        }

        if (hasError.get() == true) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, JsonUtil.toPrettyJson(elements));
        }

        return dayRuleExpr;
    }


    private Expr toParameterizableExpr(ExprInterpretor exprHandler, Element element, AtomicBoolean hasError) {
        Expr expr = null;
        try {
            expr = exprHandler.parse(element.getText());
        } catch(Exception ex) {
            element.setError("X: cannot parse");
        }
        return expr;
    }

    private void addDateRangeExprs(DayRuleExpr dayRuleExpr, Element element, AtomicBoolean hasError) {
        String elementText = element.getText();
        String[] parts = YMD_SEP_REGEX.split(elementText);
        List<String> partsClean = CommonUtil.toNoEmpty(parts);
        int k = 0;
        for (String part : partsClean) {
            YmdValueExpr ymd = exprEngine.getExprHandler(YmdValueExpr.class).parse(part);
            try {
                exprEngine.getExprHandler(YmdValueExpr.class).validate(ymd);
            } catch (HandlableException hEx) {
                element.setError(hEx.getMessage());
                hasError.set(true);
            }
            if (k == 0) {
                dayRuleExpr.setFrom(ymd);
            } else if (k == 1) {
                dayRuleExpr.setTo(ymd);
            }
            k++;
        }
    }

    public boolean inRange(DayRuleExpr dayRuleExpr, LocalDate date) {
        boolean inRange = true;

        if (dayRuleExpr.getFrom() != null && dayRuleExpr.getTo() != null && YmdValueExprInterpretor.asYearMonthDay(dayRuleExpr.getFrom()) > YmdValueExprInterpretor.asYearMonthDay(dayRuleExpr.getTo())) {
            YmdValueExpr temp = dayRuleExpr.getFrom();
            dayRuleExpr.setFrom(dayRuleExpr.getTo());
            dayRuleExpr.setTo(temp);
        }
        if (dayRuleExpr.getFrom() != null || dayRuleExpr.getTo() != null) {
            if (dayRuleExpr.getFrom() != null && YmdValueExprInterpretor.toYearMonthDayOf(dayRuleExpr.getFrom(), date) < YmdValueExprInterpretor.asYearMonthDay(dayRuleExpr.getFrom())) {
                inRange = false;
            }
            if (dayRuleExpr.getTo() != null && YmdValueExprInterpretor.toYearMonthDayOf(dayRuleExpr.getTo(), date) > YmdValueExprInterpretor.asYearMonthDay(dayRuleExpr.getTo())) {
                inRange = false;
            }
        }
        return inRange;
    }

    @Override
    public String toExpr(DayRuleExpr dayRuleExpr) {
        YmdValueExprInterpretor ymdValueExprHandler = (YmdValueExprInterpretor) exprEngine.getExprHandler(YmdValueExpr.class);
        StringBuilder exprs = new StringBuilder();
        boolean hasValueExpr = false;
        if (dayRuleExpr.getFrom() != null) {
            hasValueExpr = true;
            exprs.append(ymdValueExprHandler.toExpr(dayRuleExpr.getFrom()));
        }
        if (dayRuleExpr.getTo() != null) {
            if (hasValueExpr) {
                exprs.append(",");
                exprs.append(ymdValueExprHandler.toExpr(dayRuleExpr.getTo()));
            }
        }

        if (hasValueExpr) {
            exprs.append("; ");
        }
        for (Expr expr : dayRuleExpr.getExprs()) {
            exprs.append(exprEngine.getExprHandler(expr.getClass()).toExprString(expr));
        }
        return exprs.toString();
    }

    @Override
    public void validate(DayRuleExpr expr) throws HandlableException {
        //not implemented
    }

    @Override
    public String calculateExpr(DayRuleExpr dayRuleExpr, LocalDate date) {
        if (inRange(dayRuleExpr, date)) {
            StringBuilder result = new StringBuilder();
            for (Expr expr : dayRuleExpr.getExprs()) {
                result.append(" ").append(exprEngine.getExprHandler(expr.getClass()).calculateExprString(expr, date));
            }
            return result.toString().trim();
        } else {
            return String.valueOf(false);
        }
    }


}
