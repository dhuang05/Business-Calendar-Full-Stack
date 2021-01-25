/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;

import com.nusino.microservices.service.buscalendar.CalendarBusinessTimeService;
import com.nusino.microservices.vo.buscalendar.AvailableTimeslot;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("api/calendar/auth/businesstime")
public class CalendarBusinessTimeController {
    @Autowired
    private CalendarBusinessTimeService calendarBusinessTimeService;

    /**
     * @param calId         calendar ID
     * @param duration
     * @param sinceDateTime
     * @param localZoneId
     * @return
     */
    @GetMapping(path = "{calId}/{duration}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    AvailableTimeslot findBusinessTime(
            @PathVariable String calId,
            @PathVariable String duration,
            @RequestParam(required = false) String sinceDateTime,
            @RequestParam(required = false) String localZoneId) {

        LocalDateTime sinceLocalDateTime = null;
        if (sinceDateTime != null) {
            sinceLocalDateTime = LocalDateTime.parse(sinceDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return calendarBusinessTimeService.findBusinessTime(calId, duration, sinceLocalDateTime, localZoneId);
    }

}
