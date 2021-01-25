/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;


import com.nusino.microservices.jwt.UserTokenSession;
import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnership;
import com.nusino.microservices.model.buscalendar.Role;
import com.nusino.microservices.model.buscalendar.User;
import com.nusino.microservices.service.buscalendar.CalendarAdminService;
import com.nusino.microservices.service.buscalendar.UserAdminService;
import com.nusino.microservices.vo.buscalendar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/calendar")
public class CalendarAdminController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private CalendarAdminService calendarAdminService;

    @Autowired
    private UserAdminService userService;

    @Autowired
    private UserTokenSession userTokenSession;

    @GetMapping(path = "template/{calId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    CalendarInst fetchTemplate(HttpServletRequest request,  @PathVariable(required=false)  String calId) {
        //UserInfo userInfo = userSession.loginCheck(request);
        return calendarAdminService.fetchCalendarAdminTemplate(calId);
    }

    @GetMapping(path = "inst/{calId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    CalendarInst fetchCalendarInstance(HttpServletRequest request, @PathVariable String calId) {
        //UserInfo userInfo = userSession.loginCheck(request);
        return calendarAdminService.fetchCalendarAdminInst(calId);
    }

    @PostMapping(path = "rule/test/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    RuleExprTestResult testRuleExpression(HttpServletRequest request, @RequestBody RuleExpr ruleExpr, @PathVariable Integer year) {
        //UserInfo userInfo = userSession.loginCheck(request);
        return calendarAdminService.testRuleExpression(ruleExpr, year);
    }

    @PostMapping(path = "testsave/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    CalendarAdminInstTestResult testAndSaveCalendarAdminInst(HttpServletRequest request, @RequestBody CalendarOwnership calendarInstOwnership, @PathVariable Integer year) {
        String userId = userTokenSession.retrieveTokenUserId(request);
        CalendarAdminInstTestResult result = calendarAdminService.testAndSaveCalendarAdminInst(userId, calendarInstOwnership, year, true);
        //too much return
        result.getCalendar().setDays(null);
        return result;
    }

    @PostMapping(path = "test/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    CalendarAdminInstTestResult testCalendarAdminInst(HttpServletRequest request, @RequestBody CalendarOwnership calendarInstOwnership, @PathVariable Integer year) {
        //UserInfo userInfo = userSession.loginCheck(request);
        CalendarAdminInstTestResult result = calendarAdminService.testAndSaveCalendarAdminInst(null, calendarInstOwnership, year, false);
        //too much return
        result.getCalendar().setDays(null);
        return result;
    }

    @GetMapping(path = "accessible/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<BusinessCalendarOwnership> allAccessibleCalendars(HttpServletRequest request) {
        //userSession.storeUserInfo(request, userInfo);
        String userOrgId = null; //todo get from session or?
        List<BusinessCalendarOwnership> businessCalendarOwnerships = userService.findUserAccessibleCalendarOwnerShips(userOrgId);
        return businessCalendarOwnerships;
    }

    @GetMapping(path = "all-addon-functions", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Collection<String> fetchAllAddOnFunctions() {
        return calendarAdminService.fetchAllAddOnFunctions();
    }


}
