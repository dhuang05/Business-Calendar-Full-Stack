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

public class EasterMondayFunction extends EasterSundayFunction {
    private static String KEY_WORD = "MONDAY";

    protected String getKeyWord() {
        return KEY_WORD;
    }

    @Override
    public List<String> exprDescs() {
        return Arrays.asList(new String[] {"Easter Monday"});
    }

    @Override
    public List<LocalDate> calculate(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(super.findEasterSunday(year).plus(1, ChronoUnit.DAYS));
        return dates;
    }

}
