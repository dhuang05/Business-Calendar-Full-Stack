/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.util;

import com.nusino.microservices.service.buscalendar.expr.model.Pair;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtil {
    private final static Pattern SEP_REGEX = Pattern.compile("[,]{1,}");
    public final static Pattern PARAMS_REGEX = Pattern.compile("^([(]{1}[\\+|\\-|\\d|,|\\s]{1,}[)]{1})");
    private final static Pattern DIGIT_REGEX = Pattern.compile("[\\d]{1,}");
    private final static Pattern NON_DIGIT_REGEX = Pattern.compile("[\\D]{1,}");
    private final static LocalDate epochDay = LocalDate.ofEpochDay(0);
    private final static Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
    public static String fetchRegexAtGroup(Pattern regex, String content, int groupI) {
        if (content == null) {
            return null;
        }
        Matcher matcher = regex.matcher(content);
        if (matcher.find()) {
            if (matcher.groupCount() >= groupI) {
                return matcher.group(groupI - 1);
            }
        }
        return null;
    }

    public static String fetchRegexAtMaxLenGroup(Pattern regex, String content) {
        if (content == null) {
            return null;
        }
        Matcher matcher = regex.matcher(content);
        String max = null;
        if (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                String groupText = matcher.group(i).trim();
                if (i == 0 || (max.length() < groupText.length())) {
                    max = groupText;
                }
                return max;
            }
        }
        return null;
    }

    public static String fetchFirstNumOnRegexAtGroup(Pattern regex, String content, int groupI) {
        if (content == null) {
            return null;
        }
        content = content.trim();
        Matcher matcher = regex.matcher(content);
        if (matcher.find()) {
            if (matcher.groupCount() >= groupI) {
                List<String> nums = fetchDigits(matcher.group(groupI - 1));
                if (nums != null && nums.size() > 0) {
                    return nums.get(0);
                }
            }
        }
        return null;
    }

    public static List<String> readArray(String text) {
        List<String> elements = new ArrayList<>();
        for (String part : SEP_REGEX.split(text)) {
            if (!part.trim().isEmpty()) {
                elements.add(part.trim());
            }
        }
        return elements;
    }

    public static List<String> fetchDigits(String text) {
        List<String> elements = new ArrayList<>();
        for (String part : NON_DIGIT_REGEX.split(text)) {
            if (!part.trim().isEmpty()) {
                elements.add(part.trim());
            }
        }
        return elements;
    }

    public static List<Integer> fetchInts(String text) {
        List<Integer> elements = new ArrayList<>();
        for (String part : NON_DIGIT_REGEX.split(text)) {
            if (!part.trim().isEmpty()) {
                elements.add(Integer.valueOf(part.trim()));
            }
        }
        return elements;
    }


    public static Integer fetchFirstInt(String text) {
        for (String part : NON_DIGIT_REGEX.split(text)) {
            if (!part.trim().isEmpty()) {
                return Integer.valueOf(part.trim());
            }
        }
        return null;
    }

    public static List<String> fetchNonDigits(String text) {
        List<String> elements = new ArrayList<>();
        for (String part : DIGIT_REGEX.split(text)) {
            if (!part.trim().isEmpty()) {
                elements.add(part.trim());
            }
        }
        return elements;
    }


    public static List<String> fetchRegexAllGroup(Pattern regex, String content) {
        List<String> result = new ArrayList<>();
        if (content == null) {
            return result;
        }
        Matcher matcher = regex.matcher(content);
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                String part = matcher.group(i);
                if (!part.trim().isEmpty()) {
                    result.add(part.trim());
                }
            }
        }
        return result;
    }

    public static List<String> toNoEmpty(List<String> elements) {
        List<String> result = new ArrayList<>();
        for (String part : elements) {
            if (!part.trim().isEmpty()) {
                result.add(part.trim());
            }
        }
        return result;
    }

    public static List<String> toNoEmpty(String[] elements) {
        List<String> result = new ArrayList<>();
        for (String part : elements) {
            if (!part.trim().isEmpty()) {
                result.add(part.trim());
            }
        }
        return result;
    }

    public static long countDaysSinceEpoch(LocalDate date) {
        return ChronoUnit.DAYS.between(epochDay, date);
    }

    public static Pair<String, List<Long>> parseParamsIfAny(String expressions) {
        if (expressions == null || expressions.trim().isEmpty()) {
            return null;
        }

        List<Long> params = new ArrayList<>();
        boolean hasValidElement = true;
        while (expressions != null && !expressions.trim().isEmpty() && hasValidElement) {
            hasValidElement = false;
            String element = CommonUtil.fetchRegexAtGroup(PARAMS_REGEX, expressions, 1);
            if (element != null && !element.trim().isEmpty()) {
                hasValidElement = true;
                expressions = expressions.substring(element.length());
                element = element.substring(1, element.length() - 1);
                String[] parts = SEP_REGEX.split(element);
                for (String part : parts) {
                    params.add(Long.valueOf(part.trim()));
                }
            }
        }
        return new Pair(expressions, params);
    }

    public static String toParamsExpr(List<Long> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder expr = new StringBuilder();
        expr.append("(");
        int i = 0;
        for (long p : params) {
            if (i > 0) {
                expr.append(",");
            }
            expr.append(p);
            i++;
        }
        expr.append(")");
        return expr.toString();
    }

    public static List<LocalDate> buildParamDates(List<Long> params, LocalDate date) {
        List<LocalDate> dates = new ArrayList<>();
        if (params == null || params.size() == 0) {
            dates.add(date);
        } else {
            for (Long dayAmount : params) {
                LocalDate theDate = date.plus(dayAmount * -1, ChronoUnit.DAYS);
                dates.add(theDate);
            }
        }
        return dates;
    }

    public static String newUuidNoDash() {
        return UUID.randomUUID().toString().replaceAll("[-]{1,}", "");
    }

    public static int countChar(String text, char ch) {
        if (text == null) {
            return 0;
        }
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }


    public static boolean isEmailValid(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }
}
