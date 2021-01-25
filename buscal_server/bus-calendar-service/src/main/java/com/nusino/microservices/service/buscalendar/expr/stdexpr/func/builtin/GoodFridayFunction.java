/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin;

import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GoodFridayFunction extends EasterSundayFunction {
    private Pattern GOOD_PREFIX_REGEX = Pattern.compile("^(GOOD[\\s]{0,1})");
    private static String KEY_WORD = "FRIDAY";

    @Override
    public boolean isMyExpr(String expr) {
        if(expr == null) {
            return false;
        }
        expr = expr.toUpperCase().trim();
        if(GOOD_PREFIX_REGEX.matcher(expr.toUpperCase().trim()).find()) {
            String prefix = CommonUtil.fetchRegexAtGroup(GOOD_PREFIX_REGEX, expr,1);
            expr = expr.substring(prefix.length());
            String keyword = getKeyWord();
            return KEY_WORD.startsWith(expr.trim());
        }
        return false;
    }

    @Override
    public List<String> exprDescs() {
        return Arrays.asList(new String[] {"Good Friday"});
    }

    @Override
    public List<LocalDate> calculate(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(super.findEasterSunday(year).plus(-2, ChronoUnit.DAYS));
        return dates;
    }

}
