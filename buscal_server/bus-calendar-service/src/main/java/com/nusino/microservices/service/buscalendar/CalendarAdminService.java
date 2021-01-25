/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.exception.AuthorizationException;
import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.model.buscalendar.BusinessCalendarOwnership;
import com.nusino.microservices.model.buscalendar.Role;
import com.nusino.microservices.model.buscalendar.User;
import com.nusino.microservices.service.buscalendar.expr.model.DayRuleExpr;
import com.nusino.microservices.service.buscalendar.expr.stdexpr.func.AddOnFuncExprInterpretor;
import com.nusino.microservices.service.buscalendar.translator.TranslationEngine;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.*;
import com.nusino.microservices.vo.buscalendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CalendarAdminService {
    @Autowired
    private CalendarRepoService calendarRepoService;

    @Autowired
    private final TranslationEngine translationEngine = new TranslationEngine();
    //
    @Autowired
    private FullCalendarKeeper fullCalendarKeeper;

    @Autowired
    private UserAdminService userAdminService;

    int trialUserCalendarLimit = 1;

    //direct instance
    protected ExprEngine exprEngine = new ExprEngine(true);
    protected CalendarBuilder calendarBuilder = new CalendarBuilder(exprEngine);

    //
    public CalendarInst fetchCalendarAdminTemplate(String calId) {
        CalendarInst calendarInst = null;
        if(calId != null) {
            calendarInst = fetchCalendarAdminInst(calId);
            calendarInst.setCalId(null);
            calendarInst.setDesc(null);
        } else {
            calendarInst = fetchCalendarTemplate();
            translateToLanguage(calendarInst, Locale.ENGLISH.toString());
        }

        return calendarInst;
    }

    public CalendarInst fetchCalendarAdminInst(String calId) {
        CalendarInst calendarInst = fullCalendarKeeper.cloneCalendarInst(calId);
        translateToLanguage(calendarInst, Locale.ENGLISH.toString());
        return calendarInst;
    }

    private CalendarInst translateToLanguage(CalendarInst calendarInst, String language) {
        for (DayRule dayRule : calendarInst.getHolidayRules()) {
            DayRuleExpr dayRuleExpr = exprEngine.getDayRuleExprInterpretor().parse(dayRule.getExpr());
            String languageExr = translationEngine.toLanguage(dayRuleExpr, language);
            dayRule.setExpr(languageExr);
        }
        for (BusinessHour businessHour : calendarInst.getBusinessHours()) {
            DayRuleExpr dayRuleExpr = exprEngine.getDayRuleExprInterpretor().parse(businessHour.getDayExpr());
            String languageExr = translationEngine.toLanguage(dayRuleExpr, language);
            businessHour.setDayExpr(languageExr);
        }
        return calendarInst;
    }

    public CalendarInst fetchCalendarTemplate() {
        return calendarRepoService.loadTemplate();
    }

    //
    public Calendar createFullCalendar(CalendarInst calendarInst, Integer yearNum) {
        return calendarBuilder.buildFullCalendar(calendarInst, yearNum);
    }
    //

    public RuleExprTestResult testRuleExpression(RuleExpr ruleExpr, Integer year) {
        RuleExprTestResult result = new RuleExprTestResult();
        result.setName(ruleExpr.getName());
        result.setYear(year);
        try {
            String exprText = translationEngine.toFormula(ruleExpr.getExpr(), null);
            if (year == null) {
                year = LocalDate.now().getYear();
            }
            result.setRuleDates(exprEngine.allDaysMatchedOfYear(exprText, year));
            result.setSuccess(true);

        } catch (FeedbackableException fbEx) {
            RuleExprError ruleExprError = new RuleExprError();
            ruleExprError.setExprName(ruleExpr.getName());
            ruleExprError.setElementErrors(fbEx.getErrorElements());
            result.setRuleExprError(ruleExprError);
            result.setSuccess(false);

        } catch (RuntimeException rtEx) {
            List<Element> errors = new ArrayList<>();
            Element element = new Element();
            errors.add(element);
            element.setText(ruleExpr.getExpr());
            element.setError(rtEx.getMessage());
            RuleExprError ruleExprError = new RuleExprError();
            ruleExprError.setExprName(ruleExpr.getName());
            ruleExprError.setElementErrors(errors);
            result.setRuleExprError(ruleExprError);
            result.setSuccess(false);
        }
        return result;
    }

    public Collection<String> fetchAllAddOnFunctions() {
        return AddOnFuncExprInterpretor.fetchAllAddOnFunctions();
    }

    public CalendarAdminInstTestResult testAndSaveCalendarAdminInst(String userId,  CalendarOwnership calendarInstOwnership, Integer year, boolean toSave) {
        if (calendarInstOwnership.getCalendarInst().getCalId() == null || calendarInstOwnership.getCalendarInst().getCalId().trim().isEmpty()) {
            calendarInstOwnership.getCalendarInst().setCalId(CommonUtil.newUuidNoDash());
        }

        if (calendarInstOwnership.getCalId() == null || calendarInstOwnership.getCalId().trim().isEmpty()) {
            calendarInstOwnership.setCalId(calendarInstOwnership.getCalendarInst().getCalId());
            calendarInstOwnership.setDescription(calendarInstOwnership.getCalendarInst().getDesc());
        }

        CalendarAdminInstTestResult result = new CalendarAdminInstTestResult();
        result.setName(calendarInstOwnership.getDescription());
        result.setYear(year);
        CalendarInst calendarInst = calendarInstOwnership.getCalendarInst();
        if (calendarInst.getCalId() == null) {
            calendarInst.setCalId(CommonUtil.newUuidNoDash());
        }
        //iit value
        result.setSuccess(true);
        // converting expr from admin language to tech formula
        for (BusinessHour businessHour : calendarInst.getBusinessHours()) {
            try {
                String exprFormula = translationEngine.toFormula(businessHour.getDayExpr(), null);
                businessHour.setDayExpr(exprFormula);
            } catch (FeedbackableException fbEx) {
                result.setSuccess(false);
                RuleExprError ruleExprError = new RuleExprError();
                ruleExprError.setRuleType("Business_Hour");
                ruleExprError.setExprName(businessHour.getDesc());
                ruleExprError.setElementErrors(fbEx.getErrorElements());
                result.getRuleExprErrors().add(ruleExprError);

            } catch (RuntimeException rtEx) {
                List<Element> errors = new ArrayList<>();
                Element element = new Element();
                errors.add(element);
                element.setText(businessHour.getDesc());
                element.setError(rtEx.getMessage());

                RuleExprError ruleExprError = new RuleExprError();
                ruleExprError.setExprName(businessHour.getDesc());
                ruleExprError.setRuleType("Business_Hour");
                ruleExprError.setElementErrors(errors);
                result.getRuleExprErrors().add(ruleExprError);
                result.setSuccess(false);
            }
        }
        //
        for (DayRule dayRule : calendarInst.getHolidayRules()) {
            if (dayRule.getDayRuleId() == null || dayRule.getDayRuleId().trim().isEmpty()) {
                dayRule.setDayRuleId(toId(dayRule.getDesc()));
            }
            try {
                String exprFormula = translationEngine.toFormula(dayRule.getExpr(), null);
                dayRule.setExpr(exprFormula);

            } catch (FeedbackableException fbEx) {
                result.setSuccess(false);
                RuleExprError ruleExprError = new RuleExprError();
                ruleExprError.setRuleType("Holiday");
                ruleExprError.setExprName(dayRule.getDesc());
                ruleExprError.setElementErrors(fbEx.getErrorElements());

            } catch (RuntimeException rtEx) {
                List<Element> errors = new ArrayList<>();
                Element element = new Element();
                errors.add(element);
                element.setText(dayRule.getExpr());
                element.setError(rtEx.getMessage());

                RuleExprError ruleExprError = new RuleExprError();
                ruleExprError.setExprName(dayRule.getDesc());
                ruleExprError.setRuleType("Holiday");
                ruleExprError.setElementErrors(errors);
                result.getRuleExprErrors().add(ruleExprError);
                result.setSuccess(false);
            }
        }
        //
        Calendar calendar = calendarBuilder.buildFullCalendar(calendarInst, year);
        result.setCalendar(calendar);
        //

        if (result.getSuccess() && toSave) {
            canSaveCalOwnership(userId, calendarInstOwnership.getCalId());

            BusinessCalendarOwnership busCalOwnership = new BusinessCalendarOwnership();
            busCalOwnership.setCalId(calendarInstOwnership.getCalId());
            busCalOwnership.setVersion(calendarInstOwnership.getVersion());
            String json = JsonUtil.toJson(calendarInst);
            //decide where to save
            busCalOwnership.setCalendarInstJson(json);
            busCalOwnership.setCalendarInstUrl(calendarInstOwnership.getCalendarInstUrl());
            busCalOwnership.setOwnerId(calendarInstOwnership.getOwnerId());
            busCalOwnership.setDescription(calendarInstOwnership.getDescription());
            busCalOwnership.setStatus(calendarInstOwnership.getStatus());
            busCalOwnership.setToken(calendarInstOwnership.getToken());
            //
            calendarRepoService.saveCalendarInst(busCalOwnership);

            fullCalendarKeeper.cleanCalendarInst(busCalOwnership.getCalId());

            result.setUpdatedBusCalOwnership(busCalOwnership);
        }

        return result;
    }

    private boolean canSaveCalOwnership(String userId, String calId) {
        Optional<User> userOpt = userAdminService.findById(userId);
        if(!userOpt.isPresent()) {
            throw new AuthorizationException(AuthorizationException.CODE.REQUIRED_LOGIN, "Please login, then try again!");
        } else {
            User user = userOpt.get();
            boolean isTrialUser = false;
            for(Role role : user.getRoles()) {
                if(role.getRoleId().equalsIgnoreCase(RoleConst.TRIAL_ROLE)) {
                    isTrialUser = true;
                    break;
                }
            }
            if(!isTrialUser) {
                return true;
            }else {
                List<BusinessCalendarOwnership> ownerships = calendarRepoService.findCalendarInstOwnershipsByUserId(userId);
                if(ownerships == null || ownerships.size() < trialUserCalendarLimit) {
                    return true;
                }
                if(calId == null){
                    if(ownerships.size() >= trialUserCalendarLimit) {
                        throw new AuthorizationException(AuthorizationException.CODE.UNAUTHENTICATED, "Trial user can only has one calendar!");
                    } else{
                        return true;
                    }
                } else {
                    for(BusinessCalendarOwnership owner : ownerships) {
                        if(owner.getCalId().equals(calId)) {
                            return true;
                        }
                    }
                }
            }
        }
        throw new AuthorizationException(AuthorizationException.CODE.UNAUTHENTICATED, "Trial user can only has one calendar!");
    }

    private String toId(String desc) {
        if (desc == null || desc.trim().isEmpty()) {
            return null;
        }
        desc = desc.replaceAll("[\\s]{1,}", "_");
        return desc.toUpperCase();
    }


}
