/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin;

import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnExprHandler;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class EasterSundayFunction implements AddOnExprHandler {
    private Pattern EASTER_SUNDAY_PREFIX_REGEX = Pattern.compile("^(EASTER[\\s]{0,})");
    private static String KEY_WORD = "SUNDAY";

    @Override
    public boolean isMyExpr(String expr) {
        if(expr == null) {
            return false;
        }
        expr = expr.toUpperCase().trim();
        if(EASTER_SUNDAY_PREFIX_REGEX.matcher(expr.toUpperCase().trim()).find()) {
            String prefix = CommonUtil.fetchRegexAtGroup(EASTER_SUNDAY_PREFIX_REGEX, expr,1);
            expr = expr.substring(prefix.length());
            String keyword = getKeyWord();
            return keyword.startsWith(expr.trim());
        }
        return false;
    }

    @Override
    public List<String> exprDescs() {
        return Arrays.asList(new String[] {"Easter Sunday"});
    }

    @Override
    public List<LocalDate> calculate(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(findEasterSunday(year));
        return dates;
    }

    protected LocalDate findEasterSunday(int year) {
        List<LocalDate> dates = new ArrayList<>();
        int g = year % 19;
        int c = year / 100;
        int h = (c - (c / 4) - ((8 * c + 13) / 25) + 19 * g + 15) % 30;
        int i = h - (h / 28) * (1 - (h / 28) * (29 / (h + 1)) * ((21 - g) / 11));
        int day = (i - ((year + (year / 4) + i + 2 - c + (c / 4)) % 7) + 28);
        int month = 3;
        if (day > 31) {
            month = month + 1;
            day -= 31;
        }
        return LocalDate.of(year, month, day);
    }


    protected String getKeyWord() {
        return KEY_WORD;
    }
}
