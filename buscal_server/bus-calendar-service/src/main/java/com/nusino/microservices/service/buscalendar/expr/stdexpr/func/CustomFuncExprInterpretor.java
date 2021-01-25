/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func;

import com.nusino.microservices.exception.HandlableException;
import com.nusino.microservices.service.buscalendar.ExprEngine;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.Pair;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.CustomFuncExpr;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class CustomFuncExprInterpretor implements ExprInterpretor<CustomFuncExpr> {
    public static final Pattern FUNCTION_HEADER_EXPR = Pattern.compile("^function\\s*([A-z0-9]+)?\\s*\\((?:[^)(]+|\\((?:[^)(]+|\\([^)(]*\\))*\\))*\\)\\s*\\{");
    public final Map<String, Map<Integer, List<LocalDate>>> FUNC_DATE_STRORAGE = new HashMap<>();
    public static final String SEPARATOR = ",";

    private boolean isAdminMode = false;
    private final ExprEngine exprEngine;

    public CustomFuncExprInterpretor(ExprEngine exprEngine, boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        this.exprEngine = exprEngine;
    }

    @Override
    public boolean isMyExpr(String exprssionText) {
        if (exprssionText == null || StringUtils.isEmpty(exprssionText.trim())) {
            return false;
        }
        exprssionText = exprssionText.trim();
        return FUNCTION_HEADER_EXPR.matcher(exprssionText).find();
    }


    @Override
    public CustomFuncExpr parse(String exprssionText) {
        if (exprssionText == null) {
            return null;
        }
        CustomFuncExpr funcExpr = null;

        if (exprssionText == null || StringUtils.isEmpty(exprssionText)) {
            return null;
        }
        String someElement = fetchFunction(exprssionText);
        if (someElement != null && !StringUtils.isEmpty(someElement.trim())) {
            funcExpr = new CustomFuncExpr();
            funcExpr.setExpression(exprssionText.trim());
            funcExpr.setId(UUID.nameUUIDFromBytes(exprssionText.getBytes()).toString());
            String functName = retrieveFunctionName(someElement);
            funcExpr.setFunctionName(functName);

        }
        return funcExpr;
    }

    @Override
    public String parseParams(CustomFuncExpr funcExpr, String expressions) {
        if (funcExpr == null) {
            funcExpr = new CustomFuncExpr();
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
    public void validate(CustomFuncExpr expr) throws HandlableException {
        //N/A
    }


    public static String fetchFunction(String expressions) {
        Integer count = null;
        StringBuilder sb = new StringBuilder();
        for (char ch : expressions.toCharArray()) {
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

    private String retrieveFunctionName(String funcText) {
        funcText = funcText.trim();
        funcText = funcText.substring(funcText.indexOf(" ") + 1, funcText.indexOf("("));
        return funcText.trim();
    }


    @Override
    public String toExpr(CustomFuncExpr expr) {
        return expr.getExpression() + CommonUtil.toParamsExpr(expr.getParams());
    }

    @Override
    public String calculateExpr(CustomFuncExpr expr, LocalDate date) {
        List<LocalDate> dates = CommonUtil.buildParamDates(expr.getParams(), date);
        for (LocalDate theDate : dates) {
            boolean isMatched = true;
            List<LocalDate> customFuncDays = fetchDayOfYear(theDate.getYear(), expr);
            for (LocalDate customFuncDay : customFuncDays) {
                if (customFuncDay.getYear() != theDate.getYear() || customFuncDay.getMonthValue() != theDate.getMonthValue() || customFuncDay.getDayOfMonth() != theDate.getDayOfMonth()) {
                    isMatched = false;
                }
                if (isMatched) {
                    return String.valueOf(true);
                }
            }
        }
        return String.valueOf(false);

    }

    private List<LocalDate> fetchDayOfYear(Integer year, CustomFuncExpr expr) {
        Map<Integer, List<LocalDate>> functionValueMap = null;
        if (!isAdminMode) {
            functionValueMap = FUNC_DATE_STRORAGE.get(expr.getId());
        }

        List<LocalDate> dates = null;
        if (!isAdminMode) {
            if (functionValueMap == null) {
                functionValueMap = new HashMap<>();
                FUNC_DATE_STRORAGE.put(expr.getId(), functionValueMap);
            }
            dates = functionValueMap.get(year);
        }

        if (dates == null) {
            dates = new ArrayList<>();
            if (!isAdminMode) {
                functionValueMap.put(year, dates);
            }
            String result = exprEngine.evaluateFunction(expr, year, expr.getFunctionName());
            String[] dateTexts = result.split(SEPARATOR);
            for (String dateTxt : dateTexts) {
                try {
                    dateTxt = dateTxt.trim();
                    if (dateTxt == null || StringUtils.isEmpty(dateTxt.trim())) {
                        continue;
                    }
                    String[] parts = dateTxt.split("[-]{1,}");
                    dates.add(LocalDate.of(Integer.valueOf(parts[0].trim()), Integer.valueOf(parts[1].trim()), Integer.valueOf(parts[2].trim())));
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    throw new RuntimeException("X: Custom function:" + expr.getFunctionName() + " return value is not date '" + result + "' but should like '2020-05-09'");
                }
            }
            if (!isAdminMode) {
                functionValueMap.put(year, dates);
            }
        }
        return dates;
    }

}
