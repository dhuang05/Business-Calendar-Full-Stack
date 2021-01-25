/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func;

import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.AddOnFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin.*;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.service.buscalendar.util.KeySmith;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;


public class AddOnFuncExprInterpretor implements ExprInterpretor<AddOnFuncExpr> {
    private static final Map<String, Map<Integer, List<LocalDate>>> FUNCTION_DATE_STRORAGE = new HashMap<>();
    private boolean isAdminMode = false;
    public static Map<String, AddOnExprHandler> REGISTERED_ADD_ON_FUNCTIONS = new TreeMap<String, AddOnExprHandler>();

    static {
        addOn(new EasterSundayFunction());
        addOn(new GoodFridayFunction());
        addOn(new EasterMondayFunction());
        addOn(new MardiGrasDayFunction());
        addOn(new ChineseAddOnCalendar());
    }

    public AddOnFuncExprInterpretor(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
    }

    public static void addOn(AddOnExprHandler addOnFunc) {
        String name = addOnFunc.getClass().getName();
        if (REGISTERED_ADD_ON_FUNCTIONS.containsKey(name)) {
            System.out.println(addOnFunc.exprDescs() + " expression existing already, skip to add");
        }
        REGISTERED_ADD_ON_FUNCTIONS.put(name, addOnFunc);
    }

    public static Collection<String> fetchAllAddOnFunctions() {
        List<String> allFunctionNames = new ArrayList<>();
        for(AddOnExprHandler addOnExprHandler :  REGISTERED_ADD_ON_FUNCTIONS.values()) {
            allFunctionNames.addAll(addOnExprHandler.exprDescs());
        }
        return allFunctionNames;
    }

    @Override
    public boolean isMyExpr(String exprssionText) {
        return isMyLanguage(exprssionText);
    }

    //Language and tech is the same expression
    public static boolean isMyLanguage(String exprssionText) {
        exprssionText = exprssionText.toUpperCase();
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        for(AddOnExprHandler addOnExprHandler :  REGISTERED_ADD_ON_FUNCTIONS.values()) {
            if(addOnExprHandler.isMyExpr(exprssionText)) {
                return true;
            }
        }
       return false;
    }


    @Override
    public AddOnFuncExpr parse(String exprssionText) {
        if (exprssionText == null) {
            return null;
        }
        exprssionText = exprssionText.toUpperCase().trim();
        AddOnFuncExpr funcExpr = new AddOnFuncExpr();
        funcExpr.setExpression(exprssionText);
        funcExpr.setId(KeySmith.makeKey(exprssionText));
        return funcExpr;
    }

    @Override
    public String parseParams(AddOnFuncExpr funcExpr, String expressions) {
        if (funcExpr == null) {
            funcExpr = new AddOnFuncExpr();
        }
        Pair<String, List<Long>> result = CommonUtil.parseParamsIfAny(expressions);
        if (result != null) {
            if (result.getSecond() != null) {
                funcExpr.setParams(result.getSecond());
            }
            return result.getFirst();
        } else {
            return null;
        }

    }

    @Override
    public void validate(AddOnFuncExpr expr) throws HandlableException {
        if (!isMyExpr(expr.getExpression())) {
            throw new HandlableException(expr.getExpression() + " function is not added on yet, please add it on in spring boot configure properties.");
        }
    }

    @Override
    public String toExpr(AddOnFuncExpr expr) {
        return expr.getExpression() + CommonUtil.toParamsExpr(expr.getParams());
    }

    @Override
    public String calculateExpr(AddOnFuncExpr expr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(expr.getParams(), date);
        if(dates != null) {
            for (LocalDate theDate : dates) {
                boolean isMatched = true;
                List<LocalDate> funcDates  = fetchFuncDayOfYear(theDate.getYear(), expr.getExpression());
                for(LocalDate funcDate :  funcDates) {
                    if (funcDate.getYear() != theDate.getYear() || funcDate.getMonthValue() != theDate.getMonthValue() || funcDate.getDayOfMonth() != theDate.getDayOfMonth()) {
                        isMatched = false;
                    }
                    if (isMatched) {
                        return String.valueOf(true);
                    }
                }
            }
        }
        return String.valueOf(false);
    }

    public List<LocalDate> fetchFuncDayOfYear(Integer year, String expression) {
        String exprKey = KeySmith.makeKey(expression.toUpperCase());
        List<LocalDate> functionDatesOfYear = null;
        if (!isAdminMode) {
            Map<Integer, List<LocalDate>> functionDates = FUNCTION_DATE_STRORAGE.get(exprKey);
            if (functionDates == null) {
                functionDates = new HashMap<>();
                FUNCTION_DATE_STRORAGE.put(exprKey, functionDates);
            }
            functionDatesOfYear = functionDates.get(year);
        }
        if (functionDatesOfYear == null) {
            for(AddOnExprHandler addOnExprHandler :  REGISTERED_ADD_ON_FUNCTIONS.values()) {
                if(addOnExprHandler.isMyExpr(expression)) {
                    functionDatesOfYear =  addOnExprHandler.calculate(year, expression);
                    break;
                }
            }

            if (!isAdminMode) {
                Map<Integer, List<LocalDate>> functionDates = FUNCTION_DATE_STRORAGE.get(exprKey);
                if (functionDates == null) {
                    functionDates = new HashMap<>();
                    FUNCTION_DATE_STRORAGE.put(exprKey, functionDates);
                }
                functionDates.put(year, functionDatesOfYear);
            }
        }
        return functionDatesOfYear;
    }


}
