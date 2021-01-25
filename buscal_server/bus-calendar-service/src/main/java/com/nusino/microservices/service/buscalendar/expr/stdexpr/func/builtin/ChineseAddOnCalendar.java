/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func.builtin;

import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnExprHandler;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import net.time4j.PlainDate;
import net.time4j.calendar.ChineseCalendar;
import net.time4j.calendar.EastAsianMonth;
import net.time4j.calendar.EastAsianYear;
import net.time4j.calendar.SolarTerm;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChineseAddOnCalendar implements AddOnExprHandler {
    private static Pattern CHINESE_PREFIX_REGEX = Pattern.compile("^(CHINESE[\\s]{0,})");
    //
    private static Pattern QINGMING = Pattern.compile("^(QING[\\s]{0,}MING[\\s]{0,})$");
    private static Pattern NEW_YEAR = Pattern.compile("^(NEW[\\s]{1,}YEAR[\\s]{0,})$");
    private static Pattern NEW_YEAR_EVE = Pattern.compile("^(NEW[\\s]{1,}YEAR[\\s]{1,}EVE[\\s]{0,})$");
    private static Pattern MONTH_DAY = Pattern.compile("^([\\d]{1,2}[\\s]{0,}[/][\\s]{0,}[\\d]{1,2}(ST|ND|RD|TH){0,1}[\\s]{0,})$");

    private static Pattern[] patterns = new Pattern[] {
            QINGMING, NEW_YEAR, NEW_YEAR_EVE, MONTH_DAY
    };

    @Override
    public boolean isMyExpr(String expr) {
        if(expr == null) {
            return false;
        }
        expr = expr.toUpperCase().trim();
        if(CHINESE_PREFIX_REGEX.matcher(expr.toUpperCase().trim()).find()) {
            String prefix = CommonUtil.fetchRegexAtGroup(CHINESE_PREFIX_REGEX, expr,1);
            expr = expr.substring(prefix.length()).trim();
            for (Pattern pattern : patterns) {
                if(pattern.matcher(expr).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> exprDescs() {
        return Arrays.asList(new String[] {"Chinese QingMing", "Chinese New Year", "Chinese New Year Eve", "Chinese month/day\n --like Chinese 1/12"});
    }




    @Override
    public List<LocalDate> calculate(int year, String expr) {
        String prefix = CommonUtil.fetchRegexAtGroup(CHINESE_PREFIX_REGEX, expr,1);
        expr = expr.substring(prefix.length()).trim();
        for (Pattern pattern : patterns) {
            if(pattern.matcher(expr).find()) {
               if(pattern == MONTH_DAY) {
                   return calculateByMonthDay(year, expr);
               } else if (pattern == NEW_YEAR) {
                   return calculateByNewYear(year, expr);
               } else if (pattern == NEW_YEAR_EVE) {
                   return calculateByNewYearEve(year, expr);
               } else if (pattern == QINGMING) {
                   return calculateByQingming(year, expr);
               }
            }
        }
        return null;
    }

    private List<LocalDate> calculateByQingming(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        ChineseCalendar chineseNewYear = ChineseCalendar.ofNewYear(year);
        ChineseCalendar qingming = chineseNewYear.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015);
        dates.add(qingming.transform(PlainDate.axis()).toTemporalAccessor());
        return dates;
    }

    private List<LocalDate> calculateByNewYearEve(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        ChineseCalendar newyear = ChineseCalendar.ofNewYear(year);
        dates.add(newyear.transform(PlainDate.axis()).toTemporalAccessor().plus(-1, ChronoUnit.DAYS));
        return dates;
    }

    private List<LocalDate> calculateByNewYear(int year, String expr) {
        List<LocalDate> dates = new ArrayList<>();
        ChineseCalendar newyear = ChineseCalendar.ofNewYear(year);
        dates.add(newyear.transform(PlainDate.axis()).toTemporalAccessor());
        return dates;
    }

    private List<LocalDate> calculateByMonthDay(int year, String expr) {
        List<Integer> numbers = CommonUtil.fetchInts(expr);
        List<LocalDate> dates = new ArrayList<>();
        net.time4j.calendar.ChineseCalendar dragonBoatFestival =
                net.time4j.calendar.ChineseCalendar.of(
                        EastAsianYear.forGregorian(year),
                        EastAsianMonth.valueOf(numbers.get(0)),
                        numbers.get(1));
        dates.add(dragonBoatFestival.transform(PlainDate.class).toTemporalAccessor());
        return dates;
    }

}
