/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.vo.buscalendar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarInstService {
    protected ExprEngine exprEngine = new ExprEngine(false);

    @Autowired
    private FullCalendarKeeper fullCalendarKeeper;

    public Calendar findCalendarOfYear(String calId, Integer yearNum) {
        Calendar fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, yearNum);
        if (fullCalendarOfYear == null) {
            return null;
        }

        Calendar calendar = new Calendar();
        calendar.setCalId(fullCalendarOfYear.getCalId());
        calendar.setYear(fullCalendarOfYear.getYear());
        calendar.setDaytimeSavingFrom(fullCalendarOfYear.getDaytimeSavingFrom());
        calendar.setDaytimeSavingTo(fullCalendarOfYear.getDaytimeSavingTo());
        calendar.setDesc(fullCalendarOfYear.getDesc());
        calendar.setTimeZoneId(fullCalendarOfYear.getTimeZoneId());

        for (Holiday holiday : fullCalendarOfYear.getHolidays()) {
            calendar.getHolidays().add(holiday.clone());
        }

        for (Integer dfw : fullCalendarOfYear.getBusinessHours().keySet()) {
            DowBusinessHour businessHour = fullCalendarOfYear.getBusinessHours().get(dfw);
            calendar.getBusinessHours().put(dfw, businessHour.clone());
        }

        calendar.setOverridingBusinessHours(fullCalendarOfYear.getOverridingBusinessHours());
        for (LocalDate key : fullCalendarOfYear.getOverridingBusinessHours().keySet()) {
            OverridingBusinessHour overridingBusinessHour = fullCalendarOfYear.getOverridingBusinessHours().get(key);
            if (overridingBusinessHour != null) {
                calendar.getOverridingBusinessHours().put(key, overridingBusinessHour.clone());
            }
        }

        calendar.setLeapYear(fullCalendarOfYear.getLeapYear());
        for (Day day : fullCalendarOfYear.getDays()) {
            Day clonedDay = day.clone();
            if (clonedDay.getDayLightSaving() != null && clonedDay.getDayLightSaving() == false) {
                clonedDay.setDayLightSaving(null);
            }
            clonedDay.setBusinessDay(null);
            calendar.getDays().add(clonedDay);
        }

        return calendar;
    }

    public Calendar findCalendarInfoOfYear(String calId, Integer yearNum) {
        Calendar fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, yearNum);
        if (fullCalendarOfYear == null) {
            return null;
        }

        Calendar calendar = new Calendar();
        calendar.setCalId(fullCalendarOfYear.getCalId());
        calendar.setYear(fullCalendarOfYear.getYear());
        calendar.setDaytimeSavingFrom(fullCalendarOfYear.getDaytimeSavingFrom());
        calendar.setDaytimeSavingTo(fullCalendarOfYear.getDaytimeSavingTo());
        calendar.setDesc(fullCalendarOfYear.getDesc());
        calendar.setTimeZoneId(fullCalendarOfYear.getTimeZoneId());

        for (Holiday holiday : fullCalendarOfYear.getHolidays()) {
            calendar.getHolidays().add(holiday.clone());
        }

        for (Integer dfw : fullCalendarOfYear.getBusinessHours().keySet()) {
            DowBusinessHour businessHour = fullCalendarOfYear.getBusinessHours().get(dfw);
            calendar.getBusinessHours().put(dfw, businessHour.clone());
        }

        calendar.setOverridingBusinessHours(fullCalendarOfYear.getOverridingBusinessHours());
        for (LocalDate key : fullCalendarOfYear.getOverridingBusinessHours().keySet()) {
            OverridingBusinessHour overridingBusinessHour = fullCalendarOfYear.getOverridingBusinessHours().get(key);
            if (overridingBusinessHour != null) {
                calendar.getOverridingBusinessHours().put(key, overridingBusinessHour.clone());
            }
        }

        calendar.setLeapYear(fullCalendarOfYear.getLeapYear());
        calendar.getDays().add(fullCalendarOfYear.getDays().get(0).clone());
        return calendar;
    }

    public Calendar findCalendarOfMonth(String calId, Integer yearNum, Integer monthNum) {
        Calendar fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, yearNum);
        if (fullCalendarOfYear == null) {
            return fullCalendarOfYear;
        }

        Calendar calendar = new Calendar();
        calendar.setYear(fullCalendarOfYear.getYear());
        calendar.setCalId(fullCalendarOfYear.getCalId());
        calendar.setDaytimeSavingFrom(fullCalendarOfYear.getDaytimeSavingFrom());
        calendar.setDaytimeSavingTo(fullCalendarOfYear.getDaytimeSavingTo());
        calendar.setDesc(fullCalendarOfYear.getDesc());
        calendar.setTimeZoneId(fullCalendarOfYear.getTimeZoneId());
        for (Integer dfw : fullCalendarOfYear.getBusinessHours().keySet()) {
            DowBusinessHour businessHour = fullCalendarOfYear.getBusinessHours().get(dfw);
            calendar.getBusinessHours().put(dfw, businessHour.clone());
        }

        calendar.setLeapYear(fullCalendarOfYear.getLeapYear());
        Integer lastDayOfMonth = exprEngine.getLastDayOfMonth(yearNum, monthNum);

        List<Day> days = fullCalendarOfYear.getDays();
        long indexFrom = LocalDate.of(yearNum, monthNum, 1).toEpochDay()
                - LocalDate.of(yearNum, 1, 1).toEpochDay();
        long indexTo = LocalDate.of(yearNum, monthNum, lastDayOfMonth).toEpochDay()
                - LocalDate.of(yearNum, 1, 1).toEpochDay();

        for (int i = (int) indexFrom; i <= (int) indexTo; i++) {
            Day day = days.get(i);
            if (day.getDate().getMonthValue() == monthNum) {
                Day clonedDay = day.clone();
                if (clonedDay.getDayLightSaving() != null && clonedDay.getDayLightSaving() == false) {
                    clonedDay.setDayLightSaving(null);
                }
                clonedDay.setBusinessDay(null);
                calendar.getDays().add(clonedDay);
                OverridingBusinessHour overridingBusinessHour = fullCalendarOfYear.getOverridingBusinessHours().get(day.getDate());
                if (overridingBusinessHour != null) {
                    calendar.getOverridingBusinessHours().put(clonedDay.getDate(), overridingBusinessHour.clone());
                }
            }
            if (day.getDate().getMonthValue() > monthNum) {
                break;
            }
        }

        for (Holiday holiday : fullCalendarOfYear.getHolidays()) {
            if (holiday.getDay().getMonthValue() == monthNum) {
                calendar.getHolidays().add(holiday.clone());
            }
        }
        return calendar;
    }


}
