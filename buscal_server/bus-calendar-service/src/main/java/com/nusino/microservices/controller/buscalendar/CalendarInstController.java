/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;

import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.service.buscalendar.CalendarInstService;
import com.nusino.microservices.vo.buscalendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/calendar/inst")
public class CalendarInstController {
    @Autowired
    private CalendarInstService calendarInstService;

    @GetMapping(path = "full/{calId}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Calendar fullCalendarOfYear(@PathVariable String calId, @PathVariable Integer year) {
        Calendar calendar = calendarInstService.findCalendarOfYear(calId, year);
        if (calendar == null) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, String.format("Calendar '%s' not found!", calId));
        }
        return calendar;
    }

    @GetMapping(path = "auth/info/{calId}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Calendar calendarInfoOfYear(@PathVariable String calId, @PathVariable Integer year) {
        Calendar calendar = calendarInstService.findCalendarInfoOfYear(calId, year);
        if (calendar == null) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, String.format("Calendar '%s' not found!", calId));
        }
        return calendar;
    }

    @GetMapping(path = "full/{calId}/{year}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Calendar monthCalendarOfYear(@PathVariable String calId, @PathVariable Integer year, @PathVariable Integer month) {
        Calendar calendar = calendarInstService.findCalendarOfMonth(calId, year, month);
        if (calendar == null) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, String.format("Calendar '%s' not found!", calId));
        }
        return calendar;
    }


}
