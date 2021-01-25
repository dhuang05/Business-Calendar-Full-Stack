/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnPackagesScanner;
import com.nusino.microservices.service.buscalendar.util.KeySmith;
import com.nusino.microservices.vo.buscalendar.Calendar;
import com.nusino.microservices.vo.buscalendar.CalendarInst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Component
public class FullCalendarKeeper {
    private static final Map<String, Map<String, Calendar>> YEARLY_CALENDAR_CACHE = new HashMap<>();
    private static final Map<String, CalendarInst> CALENDAR_CACHE = new HashMap<>();

    protected ExprEngine exprEngine = new ExprEngine(false);
    protected CalendarBuilder calendarBuilder = new CalendarBuilder(exprEngine);

    @Autowired
    protected AddOnPackagesScanner addOnPackagesScanner;

    @Autowired
    private CalendarRepoService calendarRepoService;

    @PostConstruct
    public void init() {
        addOnPackagesScanner.addOnAllFunctions();
    }


    public Calendar fetchFullCalendarOfYear(String calId, Integer yearNum) {
        String key = KeySmith.makeKey(calId);
        Map<String, Calendar> calCached = YEARLY_CALENDAR_CACHE.get(key);
        if (calCached == null) {
            calCached = new HashMap<>();
            YEARLY_CALENDAR_CACHE.put(key, calCached);
        }
        Calendar temp = calCached.get(yearNum);
        if (temp != null) {
            return temp;
        } else {
            Calendar fullCalendar = createFullCalendar(calId, yearNum);
            if (fullCalendar != null) {
                calCached.put(key, fullCalendar);
            }
            return fullCalendar;
        }
    }

    /**
     * light action
     */
    public void cleanCalendarInst(String calId) {
        String key = KeySmith.makeKey(calId);
        CALENDAR_CACHE.remove(key);
        YEARLY_CALENDAR_CACHE.remove(key);
    }


    /**
     * Heavy actions
     */
    public void eraseCleanAllCalendarCache() {
        CALENDAR_CACHE.clear();
        YEARLY_CALENDAR_CACHE.clear();
    }

    public Calendar createFullCalendar(String calId, Integer yearNum) {
        CalendarInst calendarInst = findCalendarInst(calId);
        return calendarBuilder.buildFullCalendar(calendarInst, yearNum);
    }

    public CalendarInst cloneCalendarInst(String calId) {
        CalendarInst calendarInst = findCalendarInst(calId);
        if (calendarInst != null) {
            calendarInst = calendarInst.clone();
        }
        return calendarInst;
    }

    private CalendarInst findCalendarInst(String calId) {
        String key = KeySmith.makeKey(calId);
        CalendarInst calendarInst = null;
        CalendarInst temp = CALENDAR_CACHE.get(key);
        if (temp != null) {
            calendarInst = temp;
        } else {
            calendarInst = calendarRepoService.findLatestById(calId);
            if (calendarInst != null) {
                calendarInst.getBusinessHours();
                calendarInst.getHolidayRules();
                CALENDAR_CACHE.put(key, calendarInst);
            }
        }
        return calendarInst;
    }

}
