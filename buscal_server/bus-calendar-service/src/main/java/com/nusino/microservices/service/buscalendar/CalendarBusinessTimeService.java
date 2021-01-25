/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;


import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.timeslotfinder.TimeSlotFinderViaRoundAboutStrategy;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.vo.buscalendar.AvailableTimeslot;
import com.nusino.microservices.vo.buscalendar.Calendar;
import com.nusino.microservices.vo.buscalendar.Timeslot;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CalendarBusinessTimeService {
    private final static Pattern D_REGEX = Pattern.compile("[D|d]{1,}");
    private final static Pattern H_REGEX = Pattern.compile("[H|h]{1,}");
    private final static Pattern M_REGEX = Pattern.compile("[M|m]{1,}");

    private static final Map<String, TimeSlotFinder> timeSlotFinders = new HashMap<>();
    private static final String DEAULT_TIMESLOT_FINDER = "DEFAULT";


    static {
        timeSlotFinders.put(DEAULT_TIMESLOT_FINDER, new TimeSlotFinderViaRoundAboutStrategy());
    }

    @Autowired
    private FullCalendarKeeper fullCalendarKeeper;

    /**
     * @param calId
     * @param duration             format D9H9M9, D = day, H = hour, M = minutes, time slot span
     * @param requestDateTimeSince format yyyy-MM-dd'T'HH:mm:ss, find a time slot from this time; default to current time
     * @param requestTimezoneId    caller side ISO timezone ID: America/New York; default to business calendar time zone
     * @return AvailableTimeslot
     */
    public AvailableTimeslot findBusinessTime(
            String calId,
            String duration,
            LocalDateTime requestDateTimeSince,
            String requestTimezoneId) {

        TimeSlotFinder timeSlotFinder = timeSlotFinders.get(DEAULT_TIMESLOT_FINDER);
        AvailableTimeslot availableTimeslot = null;
        Integer[] dhmValues = toDayHourMinutes(duration);
        //default year to get calendar info
        Integer startYear = null;
        if (requestDateTimeSince != null) {
            startYear = requestDateTimeSince.getYear();
        } else {
            startYear = LocalDate.now().getYear();
        }
        //
        Calendar fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, startYear);
        if (fullCalendarOfYear == null) {
            return null;
        }
        ZoneId calTimezone = fullCalendarOfYear.getTimeZoneId();
        //calendar zonetime
        ZonedDateTime startDateTime = null;
        if (requestDateTimeSince != null) {
            if (requestTimezoneId != null && !requestTimezoneId.trim().isEmpty()) {
                startDateTime = requestDateTimeSince.atZone(ZoneId.of(requestTimezoneId));
                startDateTime = startDateTime.withZoneSameInstant(calTimezone);
            } else {
                startDateTime = requestDateTimeSince.atZone(calTimezone);
            }
        } else {
            startDateTime = ZonedDateTime.now(calTimezone);
        }
        //in case of beginning or end of year
        if (startDateTime.getYear() != startYear) {
            startYear = startDateTime.getYear();
            fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, startYear);
        }
        AtomicDouble durationLeft = new AtomicDouble(timeSlotFinder.asWorkingDay(dhmValues[0], dhmValues[1], dhmValues[2], fullCalendarOfYear.getStandardWorkingHoursOfDay()));

        int dayIndex = (int) (LocalDate.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth()).toEpochDay()
                - LocalDate.of(startDateTime.getYear(), 1, 1).toEpochDay());

        Timeslot calTimeslot = new Timeslot();
        boolean find = false;
        for (int k = 0; k < 2; k++) {
            timeSlotFinder.findTimeslot(fullCalendarOfYear, dayIndex, startDateTime, durationLeft, calTimeslot);
            if (calTimeslot.getStart() != null && calTimeslot.getEnd() != null) {
                find = true;
                break;
            }
            dayIndex = 0;
            startYear = startYear + k;
            fullCalendarOfYear = fullCalendarKeeper.fetchFullCalendarOfYear(calId, startYear);
            ZonedDateTime.of(LocalDateTime.of(startYear, 1, 1, 0, 0, 0, 1), calTimezone);
        }
        if (find) {
            availableTimeslot = prepareResult(calTimeslot, requestTimezoneId);
        }
        return availableTimeslot;
    }

    private AvailableTimeslot prepareResult(Timeslot calTimeslot, String requestTimezoneId) {
        AvailableTimeslot availableTimeslot = new AvailableTimeslot();
        availableTimeslot.setCalendarTimeslot(calTimeslot);
        Timeslot requestTimeslot = calTimeslot.clone();
        availableTimeslot.setRequestTimeslot(requestTimeslot);
        if (requestTimezoneId != null) {
            if (requestTimeslot.getStart() != null) {
                requestTimeslot.setStart(requestTimeslot.getStart().withZoneSameInstant(ZoneId.of(requestTimezoneId)));
            }
            if (requestTimeslot.getEnd() != null) {
                requestTimeslot.setEnd(requestTimeslot.getEnd().withZoneSameInstant(ZoneId.of(requestTimezoneId)));
            }
        }
        return availableTimeslot;
    }

    private Integer[] toDayHourMinutes(String duration) {
        Integer[] dhmValues = new Integer[]{0, 0, 0};
        if (duration != null && !StringUtils.isEmpty(duration.trim())) {
            //normalized
            duration = duration.replaceAll(D_REGEX.pattern(), "d");
            duration = duration.replaceAll(H_REGEX.pattern(), "h");
            duration = duration.replaceAll(M_REGEX.pattern(), "m");
            //
            List<String> dhm = CommonUtil.fetchNonDigits(duration);
            List<String> nums = CommonUtil.fetchDigits(duration);
            //
            if (dhm.size() == 0 || dhm.size() != nums.size()) {
                throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "The duration has wrong format, should like d4h5m30, h5m10, d1, h4, or m5 (d is day, h is hour, m is minute)");
            }
            //validate
            int d = 0;
            int h = 0;
            int m = 0;
            for (int i = 0; i < dhm.size(); i++) {
                String ch = dhm.get(i);
                String num = nums.get(i);
                switch (ch) {
                    case "d":
                        d = Integer.valueOf(num);
                        break;
                    case "h":
                        h = Integer.valueOf(num);
                        break;
                    case "m":
                        m = Integer.valueOf(num);
                        break;
                    default:
                        throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, String.format("The duration has wrong format, should like d4h5m30, h5m10, m5 (a combination of char/num pairs. here d is day, h is hour, m is minute), but '%s' found.", ch));
                }
            }
            dhmValues = new Integer[]{d, h, m};
        }
        return dhmValues;
    }

    public static void addTimeSlotFinder(TimeSlotFinder timeSlotFinder) {
        timeSlotFinders.put(timeSlotFinder.getClass().getSimpleName(), timeSlotFinder);
    }
}
