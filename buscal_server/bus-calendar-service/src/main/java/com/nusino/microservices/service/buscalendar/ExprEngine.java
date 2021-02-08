/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.service.buscalendar.expr.DayRuleExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.exprif.ExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.expr.model.Expr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.*;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.AddOnFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.model.standard.func.CustomFuncExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.*;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.CustomFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.service.buscalendar.util.KeySmith;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Pattern;

public class ExprEngine {

    //
    private final Map<String, Boolean> EVAL_RESULT_CACHE = new HashMap();
    private final Map<String, String> FUNC_EVAL_RESULT_CACHE = new HashMap();
    private final Map<String, DayRuleExpr> DAY_RULE_EXPR_OBJ_CACHE = new HashMap();
    private final Map<String, DateExpr> DATE_EXPR_OBJ_CACHE = new HashMap();
    // dynamic write need locked
    private final StampedLock LOCKER_EVAL_RESULT_CACHE = new StampedLock();
    private final StampedLock LOCKER_FUNC_EVAL_RESULT_CACHE = new StampedLock();
    private final StampedLock LOCKER_DAY_RULE_EXPR_OBJ_CACHE = new StampedLock();
    private final StampedLock LOCKER_DATE_EXPR_OBJ_CACHE = new StampedLock();


    //not required locked onetime write
    private final Map<Class, ExprInterpretor> STARDARD_EXPR_INTERPRETOR_MAP = new LinkedHashMap();
    private final Map<Class, ExprInterpretor> BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP = new LinkedHashMap();
    private final Map<Class, ExprInterpretor> EXTERNAL_FUNC_EXPR_INTERPRETOR_MAP = new LinkedHashMap();

    public final LogicExprInterpretor logicExprInterpretor = new LogicExprInterpretor();
    public final YmdValueExprInterpretor ymdValueExprInterpretor = new YmdValueExprInterpretor();
    public final List<ExprInterpretor> allExprInterpretors = new ArrayList<>();
    public final List<ExprInterpretor> allParameterizedExprInterpretors = new ArrayList<>();

    private final DayRuleExprInterpretor dayRuleExprInterpretor;
    private final Map<Class, ExprInterpretor> ALL_EXPR_INTERPRETOR_MAP = new LinkedHashMap();

    //not Nashorn ScriptEngine is threadsafe
    private static final String SCRIPT_ENGINE_NAME = "graal.js";
    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(SCRIPT_ENGINE_NAME);

    //boolean replacement for auto fixed
    private static final Map<Pattern, String> AUTO_BOOLEAN_FIX_MATRIX = new HashMap<>();


    private boolean isAdminMode = false;

    public ExprEngine() {
        dayRuleExprInterpretor = new DayRuleExprInterpretor(this, isAdminMode);
        init();
    }

    public ExprEngine(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        dayRuleExprInterpretor = new DayRuleExprInterpretor(this, isAdminMode);
        init();
    }

    private void init() {
        //
        STARDARD_EXPR_INTERPRETOR_MAP.put(DateExpr.class, new DateExprInterpretor());
        STARDARD_EXPR_INTERPRETOR_MAP.put(ValueExpr.class, new ValueExprInterpretor());
        STARDARD_EXPR_INTERPRETOR_MAP.put(LastDayOfXMonthExpr.class, new LastDayOfXMonthExprInterpretor());
        STARDARD_EXPR_INTERPRETOR_MAP.put(XDayOfWeekXInMonthXExpr.class, new XDayOfWeekXInMonthXExprInterpretor());
        STARDARD_EXPR_INTERPRETOR_MAP.put(LastXDayOfWeekXInMonthXExpr.class, new LastXDayOfWeekXInMonthXExprInterpretor());
        STARDARD_EXPR_INTERPRETOR_MAP.put(XDayOfWeekXInYearExpr.class, new XDayOfWeekXInYearExprInterpretor());
        //
        BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP.put(AddOnFuncExpr.class, new AddOnFuncExprInterpretor(isAdminMode));
        BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP.put(CustomFuncExpr.class, new CustomFuncExprInterpretor(this, isAdminMode));
        //
        ALL_EXPR_INTERPRETOR_MAP.put(DayRuleExpr.class, dayRuleExprInterpretor);
        ALL_EXPR_INTERPRETOR_MAP.putAll(STARDARD_EXPR_INTERPRETOR_MAP);
        ALL_EXPR_INTERPRETOR_MAP.putAll(BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP);

        //

        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("false[\\s]{0,}false"), "false && false");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("false[\\s]{0,}true"), "false && true");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("true[\\s]{0,}true"), "true && true");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("true[\\s]{0,}false"), "true && false");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("false[\\s]{0,}![\\s]{0,}false"), "false && ! false");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("false[\\s]{0,}![\\s]{0,}true"), "false && ! true");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("true[\\s]{0,}![\\s]{0,}true"), "true && ! true");
        AUTO_BOOLEAN_FIX_MATRIX.put(Pattern.compile("true[\\s]{0,}![\\s]{0,}false"), "true && ! false");
    }

    public DayRuleExprInterpretor getDayRuleExprInterpretor() {
        return dayRuleExprInterpretor;
    }

    public Collection<ExprInterpretor> getAllParameterizedExprInterpretors() {
        if (allParameterizedExprInterpretors.size() == 0) {
            allParameterizedExprInterpretors.addAll(STARDARD_EXPR_INTERPRETOR_MAP.values());
            allParameterizedExprInterpretors.addAll(BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP.values());
            allParameterizedExprInterpretors.addAll(EXTERNAL_FUNC_EXPR_INTERPRETOR_MAP.values());
        }
        return allParameterizedExprInterpretors;
    }

    public Collection<ExprInterpretor> getAllExprInterpretors() {
        if (allExprInterpretors.size() == 0) {
            allExprInterpretors.addAll(STARDARD_EXPR_INTERPRETOR_MAP.values());
            allExprInterpretors.addAll(BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP.values());
            allExprInterpretors.addAll(EXTERNAL_FUNC_EXPR_INTERPRETOR_MAP.values());
            allExprInterpretors.add(logicExprInterpretor);
            allExprInterpretors.add(ymdValueExprInterpretor);
        }
        return allExprInterpretors;
    }


    public <T extends Expr> void registerExternalFuncExpr(Class<T> clazz, ExprInterpretor<T> exprInterpretor) {
        EXTERNAL_FUNC_EXPR_INTERPRETOR_MAP.put(clazz, exprInterpretor);
    }

    public <T extends Expr> ExprInterpretor<T> getExprHandler(Class<T> clazz) {
        ExprInterpretor<T> interpretor = STARDARD_EXPR_INTERPRETOR_MAP.get(clazz);
        if (interpretor == null) {
            interpretor = BUILT_IN_FUNC_EXPR_INTERPRETOR_MAP.get(clazz);
        }
        if (interpretor == null) {
            interpretor = EXTERNAL_FUNC_EXPR_INTERPRETOR_MAP.get(clazz);
        }
        if (interpretor == null) {
            if (clazz == LogicExpr.class) {
                interpretor = (ExprInterpretor) logicExprInterpretor;
            } else if (clazz == YmdValueExpr.class) {
                interpretor = (ExprInterpretor) ymdValueExprInterpretor;
            }
        }
        return interpretor;
    }

    public List<LocalDate> allDaysMatchedOfYear(String expression, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        return allDaysMatched(expression, from, to);
    }

    public List<LocalDate> allDaysMatched(String expression, LocalDate from, LocalDate to) {
        List<LocalDate> ruleDays = new ArrayList<>();

        Expr dayRuleExpr = genDayRuleExpr(expression);
        if (CommonUtil.countDaysSinceEpoch(from) > CommonUtil.countDaysSinceEpoch(to)) {
            LocalDate temp = from;
            from = to;
            to = temp;
        }

        LocalDate current = from;
        while (CommonUtil.countDaysSinceEpoch(current) <= CommonUtil.countDaysSinceEpoch(to)) {
            String boolExpr = ALL_EXPR_INTERPRETOR_MAP.get(dayRuleExpr.getClass()).calculateExpr(dayRuleExpr, current);
            if (evalBooleanFormula(boolExpr, expression)) {
                ruleDays.add(current);
            }
            current = current.plus(1, ChronoUnit.DAYS);
        }
        return ruleDays;
    }

    public LocalDate firstDayMatched(String expression, LocalDate from, LocalDate to) {
        LocalDate firstMatched = null;
        Expr dayRuleExpr = genDayRuleExpr(expression);
        if (CommonUtil.countDaysSinceEpoch(from) > CommonUtil.countDaysSinceEpoch(to)) {
            LocalDate temp = from;
            from = to;
            to = temp;
        }

        LocalDate current = from;
        while (CommonUtil.countDaysSinceEpoch(current) <= CommonUtil.countDaysSinceEpoch(to)) {
            String boolExpr = ALL_EXPR_INTERPRETOR_MAP.get(dayRuleExpr.getClass()).calculateExpr(dayRuleExpr, current);
            if (evalBooleanFormula(boolExpr, expression)) {
                firstMatched = current;
                break;
            }
            current = current.plus(1, ChronoUnit.DAYS);
        }
        return firstMatched;
    }

    public boolean isDayMatched(LocalDate date, String expression) {
        DayRuleExpr dayRuleExpr = genDayRuleExpr(expression);
        String boolExpr = dayRuleExprInterpretor.calculateExpr(dayRuleExpr, date);
        return evalBooleanFormula(boolExpr, expression);
    }

    public DayRuleExpr genDayRuleExpr(String expression) {
        String key = KeySmith.makeKey(expression);

        DayRuleExpr expr = null;

        long stamp = LOCKER_DAY_RULE_EXPR_OBJ_CACHE.readLock();
        try {
            expr = DAY_RULE_EXPR_OBJ_CACHE.get(key);
        }finally {
            LOCKER_DAY_RULE_EXPR_OBJ_CACHE.unlockRead(stamp);
        }
        if (expr == null) {
            expr = dayRuleExprInterpretor.parse(expression);
            stamp = LOCKER_DAY_RULE_EXPR_OBJ_CACHE.writeLock();
            try {
                DAY_RULE_EXPR_OBJ_CACHE.put(key, expr);
            }finally{
                LOCKER_DAY_RULE_EXPR_OBJ_CACHE.unlockWrite(stamp);
            }
        }
        return expr;
    }


    public boolean evalBooleanFormula(String expr, String sourceMsg) {
        Boolean val = null;
        if (!isAdminMode) {
            long stamp = LOCKER_EVAL_RESULT_CACHE.readLock();
            try {
                val = EVAL_RESULT_CACHE.get(expr);
            }finally {
                LOCKER_EVAL_RESULT_CACHE.unlockRead(stamp);
            }
        }

        if (val != null) {
            return val;
        }
        try {
            for (Pattern pattern : AUTO_BOOLEAN_FIX_MATRIX.keySet()) {
                expr = expr.replaceAll(pattern.pattern(), AUTO_BOOLEAN_FIX_MATRIX.get(pattern));
            }

            Boolean ret = (Boolean) scriptEngine.eval(expr);
            if (!isAdminMode) {
                long stamp = LOCKER_EVAL_RESULT_CACHE.writeLock();
                try {
                    EVAL_RESULT_CACHE.put(expr, ret);
                }finally {
                    LOCKER_EVAL_RESULT_CACHE.unlockWrite(stamp);
                }
            }
            return ret;
        } catch (ScriptException | RuntimeException sEx) {
            //log
            String msg = "Not calculable, Possible cause: 1. Missing 'and', 'or' between booleans. 2. Parameter not in the end of unit. 3. Not a logic expression.";

            int paramCount = CommonUtil.countChar(expr, '(') - CommonUtil.countChar(expr, ')');
            if (paramCount != 0) {
                if (paramCount > 0) {
                    msg = "Error: " + paramCount + "')' more than '('.";
                } else {
                    msg = "Error: " + (-1 * paramCount) + " '(' more than ')'.";
                }
            }
            //System.err.println("X: " + expr + " evaluated on " + sourceMsg);
            expr = expr.toUpperCase();
            expr = expr.replaceAll("(TRUE|FALSE){1}", "B");
            throw new RuntimeException(msg + " Its Boolean (B) expression: '" + expr.toUpperCase() + "'");
        }
    }

    public String evaluateFunction(CustomFuncExpr expr, int year, String functionName) {
        String val = null;
        if (!isAdminMode) {
            long stamp =  LOCKER_FUNC_EVAL_RESULT_CACHE.readLock();
            try {
                val = FUNC_EVAL_RESULT_CACHE.get(expr.getId());
            }finally {
                LOCKER_FUNC_EVAL_RESULT_CACHE.unlockRead(stamp);
            }
        }
        if (val != null) {
            return val;
        }
        try {
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(SCRIPT_ENGINE_NAME);
            scriptEngine.eval(expr.getExpression());
            Invocable invocableEngine = (Invocable) scriptEngine;
            Object result = invocableEngine.invokeFunction(functionName, year);
            String ret = result != null ? result.toString() : null;
            if (!isAdminMode) {
                long stamp =  LOCKER_FUNC_EVAL_RESULT_CACHE.writeLock();
                try {
                    FUNC_EVAL_RESULT_CACHE.put(expr.getId(), ret);
                }finally {
                    LOCKER_FUNC_EVAL_RESULT_CACHE.unlockWrite(stamp);
                }
            }
            return ret;
        } catch (ScriptException | RuntimeException | NoSuchMethodException sEx) {
            sEx.printStackTrace();
            //log
            //System.err.println("X: function cannot be evaluated:\n" + expr);
            throw new RuntimeException("Function cannot be evaluated:\n" + expr);
        }
    }


    public Integer getLastDayOfMonth(Integer year, Integer month) {
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    public Integer toDayOfWeek(String dateExprText) {
        String key = KeySmith.makeKey(dateExprText);
        DateExpr dateExpr = null;
        long stamp = LOCKER_DATE_EXPR_OBJ_CACHE.readLock();
        try {
            dateExpr = DATE_EXPR_OBJ_CACHE.get(key);
        }finally{
            LOCKER_DATE_EXPR_OBJ_CACHE.unlockRead(stamp);
        }

        if (dateExpr == null) {
            DateExprInterpretor exprHandler = (DateExprInterpretor) STARDARD_EXPR_INTERPRETOR_MAP.get(DateExpr.class);
            dateExpr = exprHandler.parse(dateExprText);
            stamp = LOCKER_DATE_EXPR_OBJ_CACHE.writeLock();
            try {
                DATE_EXPR_OBJ_CACHE.put(key, dateExpr);
            }finally{
                LOCKER_DATE_EXPR_OBJ_CACHE.unlockWrite(stamp);
            }
        }
        return dateExpr.getDayOfWeek();
    }
}
