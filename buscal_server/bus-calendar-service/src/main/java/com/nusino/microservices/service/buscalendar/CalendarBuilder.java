/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.vo.buscalendar.Calendar;
import com.nusino.microservices.vo.buscalendar.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRules;
import java.util.*;

public class CalendarBuilder {
    private final ExprEngine exprEngine;

    public CalendarBuilder(ExprEngine exprEngine) {
        this.exprEngine = exprEngine;
    }

    public Calendar buildFullCalendar(CalendarInst calendarInst, Integer yearNum) {
        // System.out.println(JsonUtil.toPrettyJson(calendarInst));
        if (calendarInst == null || calendarInst.getCalId() == null) {
            return null;
        }
        LocalDate from = LocalDate.of(yearNum, 1, 1);
        LocalDate lastDay = LocalDate.of(yearNum, 12, 31);
        //
        Calendar fullCalendar = createCalendar(calendarInst, yearNum);
        convertBusinessHours(calendarInst, fullCalendar, from, lastDay);
        //
        Map<Integer, Integer> workingHourStaticsMap = new HashMap<>();
        final LocalDate epochDay = LocalDate.ofEpochDay(0);
        List<DayRule> dayRules = new ArrayList<>();
        dayRules.addAll(calendarInst.getHolidayRules());
        Collections.unmodifiableCollection(dayRules);
        LocalDate dstFrom = null;
        LocalDate dstTo = null;
        LocalTime lunchTime = LocalTime.of(12, 00);
        //
        while (ChronoUnit.DAYS.between(epochDay, from) <= ChronoUnit.DAYS.between(epochDay, lastDay)) {
            Day day = new Day();
            day.setDate(LocalDate.of(from.getYear(), from.getMonth(), from.getDayOfMonth()));

            //business hour
            DowBusinessHour businessHour = fullCalendar.getBusinessHours().get(from.getDayOfWeek().getValue());
            boolean isWorkingDay = false;
            if (businessHour != null) {
                day.setBusinessTimeFrom(businessHour.getBusinessTimeFrom());
                day.setBusinessTimeTo(businessHour.getBusinessTimeTo());
                isWorkingDay = true;
            }
            //
            boolean isSpecialHour = false;
            OverridingBusinessHour overridingBusinessHour = fullCalendar.getOverridingBusinessHours().get(from);
            if (overridingBusinessHour != null) {
                day.setBusinessTimeFrom(overridingBusinessHour.getBusinessTimeFrom());
                day.setBusinessTimeTo(overridingBusinessHour.getBusinessTimeTo());
                isWorkingDay = true;
                isSpecialHour = true;
            }
            ZoneRules zoneRules = fullCalendar.getTimeZoneId().getRules();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(day.getDate(), lunchTime, fullCalendar.getTimeZoneId());
            Boolean isDst = zoneRules.isDaylightSavings(zonedDateTime.toInstant());
            day.setDayLightSaving(isDst);
            //
            if (isDst == true) {
                if (dstFrom == null) {
                    dstFrom = day.getDate();
                }
                dstTo = day.getDate();
            }
            int dow = from.getDayOfWeek().getValue();
            day.setDow((short) dow);

            for (DayRule dayRule : dayRules) {
                if (exprEngine.isDayMatched(from, dayRule.getExpr())) {
                    Holiday holiday = createHoliday(from, dayRule);
                    fullCalendar.getHolidays().add(holiday);
                    day.setHoliday(holiday);
                    isWorkingDay = false;
                    break;
                }
            }
            if (isWorkingDay) {
                day.setBusinessDay(true);
                if (isSpecialHour) {
                    day.setOverridingWorkingHour(true);
                    OverridingBusinessHour overridingHour = createOverridingBusinessHour(day);
                    fullCalendar.getOverridingBusinessHours().put(day.getDate(), overridingHour);
                }

            } else {
                day.setBusinessTimeFrom(null);
                day.setBusinessTimeTo(null);
            }
            statisticsWorkingHours(workingHourStaticsMap, day);
            fullCalendar.getDays().add(day);
            fullCalendar.setDaytimeSavingFrom(dstFrom);
            fullCalendar.setDaytimeSavingTo(dstTo);
            from = from.plus(1, ChronoUnit.DAYS);
        }
        fullCalendar.setDaytimeSavingFrom(dstFrom);
        fullCalendar.setDaytimeSavingTo(dstTo);
        int standardWorkingMinutes = fetchLargest(workingHourStaticsMap);
        fullCalendar.setStandardWorkingHoursOfDay(standardWorkingMinutes / 60.0);
        return fullCalendar;
    }

    private void statisticsWorkingHours(Map<Integer, Integer> workingHourStaticsMap, Day day) {
        if (day.getBusinessTimeFrom() == null || day.getBusinessTimeTo() == null) {
            return;
        }
        Integer workingMinutesOfDay = (int) ChronoUnit.MINUTES.between(day.getBusinessTimeFrom(), day.getBusinessTimeTo());
        Integer count = workingHourStaticsMap.get(workingMinutesOfDay);
        if (count == null) {
            count = 0;
        }
        workingHourStaticsMap.put(workingMinutesOfDay, count + 1);
    }

    private Calendar createCalendar(CalendarInst calendarInst, Integer yearNum) {
        Calendar fullCalendar = new Calendar();
        fullCalendar.setYear(yearNum);
        fullCalendar.setLeapYear(LocalDate.of(yearNum, 1, 1).isLeapYear());
        fullCalendar.setCalId(calendarInst.getCalId());
        fullCalendar.setDesc(calendarInst.getDesc());
        fullCalendar.setTimeZoneId(ZoneId.of(calendarInst.getTimeZone()));
        return fullCalendar;
    }

    private OverridingBusinessHour createOverridingBusinessHour(Day day) {
        OverridingBusinessHour overridingHour = new OverridingBusinessHour();
        overridingHour.setDay(day.getDate());
        overridingHour.setBusinessTimeFrom(day.getBusinessTimeFrom());
        overridingHour.setBusinessTimeTo(day.getBusinessTimeTo());
        return overridingHour;
    }

    private Holiday createHoliday(LocalDate from, DayRule dayRule) {
        Holiday holiday = new Holiday();
        holiday.setDay(from);
        holiday.setName(dayRule.getDesc());
        holiday.setRuleId(dayRule.getDayRuleId());
        return holiday;
    }

    private void convertBusinessHours(CalendarInst calendarInst, Calendar fullCalendar, LocalDate from, LocalDate to) {
        for (BusinessHour businessHour : calendarInst.getBusinessHours()) {
            if (!businessHour.getOverriding()) {
                Integer dow = exprEngine.toDayOfWeek(businessHour.getDayExpr());
                DowBusinessHour dowBusHour = new DowBusinessHour();
                dowBusHour.setBusinessTimeFrom(businessHour.getBusinessHourFrom());
                dowBusHour.setBusinessTimeTo(businessHour.getBusinessHourTo());
                dowBusHour.setDayOfWeek(dow);
                fullCalendar.getBusinessHours().put(dow, dowBusHour);
            } else {
                String dayExpr = businessHour.getDayExpr();
                List<LocalDate> overridingDates = findAllMatched(from, to, dayExpr);
                for (LocalDate date : overridingDates) {
                    OverridingBusinessHour overridingBusinessHour = new OverridingBusinessHour();
                    overridingBusinessHour.setBusinessTimeFrom(businessHour.getBusinessHourFrom());
                    overridingBusinessHour.setBusinessTimeTo(businessHour.getBusinessHourTo());
                    overridingBusinessHour.setDay(date);
                    fullCalendar.getOverridingBusinessHours().put(date, overridingBusinessHour);
                }
            }
        }
    }

    private List<LocalDate> findAllMatched(LocalDate start, LocalDate end, String dayExpr) {
        List<LocalDate> matched = new ArrayList<>();
        final LocalDate epochDay = LocalDate.ofEpochDay(0);
        LocalDate from = start;
        while (ChronoUnit.DAYS.between(epochDay, from) <= ChronoUnit.DAYS.between(epochDay, end)) {
            LocalDate fromDate = LocalDate.of(from.getYear(), from.getMonth(), from.getDayOfMonth());
            if (exprEngine.isDayMatched(from, dayExpr)) {
                matched.add(fromDate);
            }
            from = from.plus(1, ChronoUnit.DAYS);
        }
        return matched;
    }

    private int fetchLargest(Map<Integer, Integer> workingHourStaticsMap) {
        Integer numWithMaxCount = null;
        int maxCount = 0;
        for (Integer num : workingHourStaticsMap.keySet()) {
            if (maxCount < workingHourStaticsMap.get(num)) {
                maxCount = workingHourStaticsMap.get(num);
                numWithMaxCount = num;
            }
        }
        return numWithMaxCount;
    }

    private Boolean isLeapYear(Integer year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return year % 400 == 0;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}

