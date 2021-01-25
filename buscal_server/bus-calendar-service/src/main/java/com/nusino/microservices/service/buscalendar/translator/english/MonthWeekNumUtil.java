/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.translator.english;

import com.nusino.microservices.service.buscalendar.util.KeySmith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthWeekNumUtil {
    private static final Map<String, Integer> MONTH2NUM = new HashMap<>();
    private static final Map<String, String> MONTH2FORMULA = new HashMap<>();
    private static final Map<Integer, String> NUM2MONTH = new HashMap<>();

    private static final Map<String, Integer> DOW2NUM = new HashMap<>();
    private static final Map<String, String> DOW2FORMULA = new HashMap<>();
    private static final Map<Integer, String> NUM2DOW = new HashMap<>();

    public static List<String> MONTH_NAMES;
    public static List<String> DOW_NAMES;

    static {
        //Month
        MONTH2NUM.put(KeySmith.makeKey("JAN"), 1);
        MONTH2NUM.put(KeySmith.makeKey("FEB"), 2);
        MONTH2NUM.put(KeySmith.makeKey("MAR"), 3);
        MONTH2NUM.put(KeySmith.makeKey("APR"), 4);
        MONTH2NUM.put(KeySmith.makeKey("MAY"), 5);
        MONTH2NUM.put(KeySmith.makeKey("JUN"), 6);
        MONTH2NUM.put(KeySmith.makeKey("JUL"), 7);
        MONTH2NUM.put(KeySmith.makeKey("AUG"), 8);
        MONTH2NUM.put(KeySmith.makeKey("SEP"), 9);
        MONTH2NUM.put(KeySmith.makeKey("OCT"), 10);
        MONTH2NUM.put(KeySmith.makeKey("NOV"), 11);
        MONTH2NUM.put(KeySmith.makeKey("DEC"), 12);
        //
        MONTH2FORMULA.put(KeySmith.makeKey("JAN"), "M1");
        MONTH2FORMULA.put(KeySmith.makeKey("FEB"), "M2");
        MONTH2FORMULA.put(KeySmith.makeKey("MAR"), "M3");
        MONTH2FORMULA.put(KeySmith.makeKey("APR"), "M4");
        MONTH2FORMULA.put(KeySmith.makeKey("MAY"), "M5");
        MONTH2FORMULA.put(KeySmith.makeKey("JUN"), "M6");
        MONTH2FORMULA.put(KeySmith.makeKey("JUL"), "M7");
        MONTH2FORMULA.put(KeySmith.makeKey("AUG"), "M8");
        MONTH2FORMULA.put(KeySmith.makeKey("SEP"), "M9");
        MONTH2FORMULA.put(KeySmith.makeKey("OCT"), "M10");
        MONTH2FORMULA.put(KeySmith.makeKey("NOV"), "M11");
        MONTH2FORMULA.put(KeySmith.makeKey("DEC"), "M12");
        //
        NUM2MONTH.put(1, "January");
        NUM2MONTH.put(2, "February");
        NUM2MONTH.put(3, "March");
        NUM2MONTH.put(4, "April");
        NUM2MONTH.put(5, "May");
        NUM2MONTH.put(6, "June");
        NUM2MONTH.put(7, "July");
        NUM2MONTH.put(8, "August");
        NUM2MONTH.put(9, "September");
        NUM2MONTH.put(10, "October");
        NUM2MONTH.put(11, "November");
        NUM2MONTH.put(12, "December");

        //Day of week
        DOW2NUM.put(KeySmith.makeKey("MON"), 1);
        DOW2NUM.put(KeySmith.makeKey("TUE"), 2);
        DOW2NUM.put(KeySmith.makeKey("WEN"), 3);
        DOW2NUM.put(KeySmith.makeKey("THU"), 4);
        DOW2NUM.put(KeySmith.makeKey("FRI"), 5);
        DOW2NUM.put(KeySmith.makeKey("SAT"), 6);
        DOW2NUM.put(KeySmith.makeKey("SUN"), 7);

        DOW2FORMULA.put(KeySmith.makeKey("MON"), "E1");
        DOW2FORMULA.put(KeySmith.makeKey("TUE"), "E2");
        DOW2FORMULA.put(KeySmith.makeKey("WED"), "E3");
        DOW2FORMULA.put(KeySmith.makeKey("THU"), "E4");
        DOW2FORMULA.put(KeySmith.makeKey("FRI"), "E5");
        DOW2FORMULA.put(KeySmith.makeKey("SAT"), "E6");
        DOW2FORMULA.put(KeySmith.makeKey("SUN"), "E7");

        NUM2DOW.put(1, "Monday");
        NUM2DOW.put(2, "Tuesday");
        NUM2DOW.put(3, "Wednesday");
        NUM2DOW.put(4, "Thursday");
        NUM2DOW.put(5, "Friday");
        NUM2DOW.put(6, "Saturday");
        NUM2DOW.put(7, "Sunday");

        //
        MONTH_NAMES = MonthWeekNumUtil.getMonthNames();
        DOW_NAMES = MonthWeekNumUtil.getDayOfWeekNames();
    }

    public static List<String> getMonthNames() {
        return new ArrayList(NUM2MONTH.values());
    }

    public static List<String> getDayOfWeekNames() {
        return new ArrayList(NUM2DOW.values());
    }


    public static Integer month2Num(String text) {
        text = text.trim();
        text = text.substring(0, 3);
        return MONTH2NUM.get(KeySmith.makeKey(text));
    }

    public static String month2Formula(String text) {
        text = text.trim();
        text = text.substring(0, 3);
        return MONTH2FORMULA.get(KeySmith.makeKey(text));
    }

    public static String num2Month(Integer num) {
        return NUM2MONTH.get(num);
    }

    //
    public static Integer dayOfWeek2Num(String text) {
        text = text.trim();
        text = text.substring(0, 3);
        return DOW2NUM.get(KeySmith.makeKey(text));
    }

    public static String dayOfWeek2Formula(String text) {
        text = text.trim();
        text = text.substring(0, 3);
        return DOW2FORMULA.get(KeySmith.makeKey(text));
    }

    public static String num2DayOfWeek(Integer num) {
        return NUM2DOW.get(num);
    }

    public static String num2Order(String numText) {
        if (numText == null) {
            return null;
        }
        return num2Order(Integer.valueOf(numText.trim()));
    }

    public static String num2Order(Integer num) {
        int r = num % 10;
        if (r == 1) {
            return num + "st";
        } else if (r == 2) {
            return num + "nd";
        } else if (r == 3) {
            return num + "rd";
        } else {
            return num + "th";
        }
    }

    public static boolean isMonth(String text) {
        if (text.trim().length() < 3) {
            return false;
        }
        for (String month : MONTH_NAMES) {
            if (month.toUpperCase().startsWith(text.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }


    public static boolean isDayOfWeek(String text) {
        if (text.trim().length() < 2) {
            return false;
        }
        for (String dayOfWeek : DOW_NAMES) {
            if (dayOfWeek.toUpperCase().startsWith(text.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

}
