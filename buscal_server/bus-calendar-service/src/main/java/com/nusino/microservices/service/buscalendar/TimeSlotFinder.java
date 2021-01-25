/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.vo.buscalendar.Calendar;
import com.nusino.microservices.vo.buscalendar.Timeslot;

import java.time.ZonedDateTime;

public interface TimeSlotFinder {
    String strategyName();

    default Double asWorkingDay(int days, int hours, int minutes, double workingHoursOfDay) {
        double workingMinutesOfDay = Math.floor(workingHoursOfDay * 60);
        int totalMinutes = hours * 60 + minutes;
        return days + totalMinutes / workingMinutesOfDay;
    }

    default int asWorkingMinutes(double asDay, double workingHoursOfDay) {
        double workingMinutesOfDay = workingHoursOfDay * 60;
        return (int) (asDay * workingMinutesOfDay);
    }

    void findTimeslot(Calendar fullCalendarOfYear, int startDayIndex, ZonedDateTime startDateTime, AtomicDouble duration, Timeslot timeSlotStart);
}
