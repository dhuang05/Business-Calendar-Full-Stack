/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.timeslotfinder;

import com.nusino.microservices.service.buscalendar.AtomicDouble;
import com.nusino.microservices.service.buscalendar.TimeSlotFinder;
import com.nusino.microservices.vo.buscalendar.Calendar;
import com.nusino.microservices.vo.buscalendar.Day;
import com.nusino.microservices.vo.buscalendar.Timeslot;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeSlotFinderViaRoundAboutStrategy implements TimeSlotFinder {
    public String strategyName() {
        return "RoundAboutStrategy";
    }

    public void findTimeslot(Calendar fullCalendarOfYear, int startDayIndex, ZonedDateTime startDateTime, AtomicDouble duration, Timeslot timeSlotStart) {
        int standardWorkingMinutes = (int) Math.floor(fullCalendarOfYear.getStandardWorkingHoursOfDay() * 60);

        for (int i = startDayIndex; i < fullCalendarOfYear.getDays().size(); i++) {
            Day day = fullCalendarOfYear.getDays().get(i);
            if (day.getBusinessDay() == null || !day.getBusinessDay()) {
                startDateTime = ZonedDateTime.of(LocalDateTime.of(fullCalendarOfYear.getYear(), day.getDate().getMonthValue(), day.getDate().getDayOfMonth(), 0, 0, 0, 0), fullCalendarOfYear.getTimeZoneId());
                continue;
            }

            ZonedDateTime fromBusinessDateTime = ZonedDateTime.of(day.getDate(), day.getBusinessTimeFrom(), fullCalendarOfYear.getTimeZoneId());
            ZonedDateTime toBusinessDateTime = ZonedDateTime.of(day.getDate(), day.getBusinessTimeTo(), fullCalendarOfYear.getTimeZoneId());
            //Timeslot start
            if (timeSlotStart.getStart() == null) {
                if (startDateTime.isBefore(fromBusinessDateTime)) {
                    timeSlotStart.setStart(fromBusinessDateTime);
                } else if (startDateTime.isBefore(toBusinessDateTime) && startDateTime.isAfter(fromBusinessDateTime)) {
                    timeSlotStart.setStart(startDateTime);
                }
            }

            // to end possible death loop, just in case.
            if (duration.get() <= 0) {
                if (timeSlotStart.getEnd() == null) {
                    timeSlotStart.setEnd(fromBusinessDateTime);
                }
                return;
            }
            if (toBusinessDateTime.isBefore(startDateTime)) {
                continue;
            }
            //
            boolean isWholeDay = false;
            if (startDateTime.isBefore(fromBusinessDateTime)) {
                isWholeDay = true;
                startDateTime = fromBusinessDateTime;
            }

            if (isWholeDay) {
                //if weekly working day is not full day, then set isWholeDay = false;
                double wholeWorkingdayMinutes = 1.0 * ChronoUnit.MINUTES.between(startDateTime, toBusinessDateTime) / standardWorkingMinutes;
                if (wholeWorkingdayMinutes < 0.6) {
                    isWholeDay = false;
                }
            }

            if (isWholeDay && duration.get() >= 1) {
                duration.set(duration.get() - 1);
                if (duration.get() == 0) {
                    timeSlotStart.setEnd(toBusinessDateTime);
                    return;
                }
                continue;
            } else {
                int minutes = (int) ChronoUnit.MINUTES.between(startDateTime, toBusinessDateTime);
                double asDayNum = asWorkingDay(0, 0, minutes, fullCalendarOfYear.getStandardWorkingHoursOfDay());
                if (asDayNum > 1) {
                    asDayNum = 1;
                }
                if (duration.get() >= asDayNum) {
                    duration.set(duration.get() - asDayNum);
                } else {
                    int reminderMinutes = asWorkingMinutes(duration.get(), fullCalendarOfYear.getStandardWorkingHoursOfDay());
                    timeSlotStart.setEnd(startDateTime.plus(reminderMinutes, ChronoUnit.MINUTES));
                    duration.set(0);
                    return;
                }
            }
        }
    }

    public Double asWorkingDay(int days, int hours, int minutes, double workingHoursOfDay) {
        double workingMinutesOfDay = Math.floor(workingHoursOfDay * 60);
        int totalMinutes = hours * 60 + minutes;
        return days + totalMinutes / workingMinutesOfDay;
    }

    public int asWorkingMinutes(double asDay, double workingHoursOfDay) {
        double workingMinutesOfDay = workingHoursOfDay * 60;
        return (int) (asDay * workingMinutesOfDay);
    }


}
