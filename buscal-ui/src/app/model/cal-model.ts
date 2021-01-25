
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
import { Util } from "../common/util";

export class LoginForm {
        userId!: string;
        email!: string;
        password!: string;
        newPassword!: string;
}

export class BusinessCalendarOwnership {
        calId!: string;
        version!: number;
        description!: string;
        calendarInst!: CalendarInst;
        ownerId!: string;
        status: string = "ACTIVE";
        note!: string;
        isPublic: boolean = false;
}

export class CalendarInst {
        calId!: string;
        desc!: string;
        status ?: string;
        timeZone!: string;
        businessHours!: BusinessHour[];
        holidayRules!: DayRule[];
}

export class BusinessHour {
        dayExpr!: string;
        desc!: string;
        overriding!: boolean;
        //HH!:mm!:ss
        businessHourFrom!: string;
        //HH!:mm!:ss
        businessHourTo!: string;
}

export class DayRule {
        dayRuleId!: string;
        desc!: string;
        expr!: string;
        effectiveDate!: Date | undefined;
        expiredDate!: Date | undefined;
}

export class AvailableTimeslot {
        calendarTimeslot!: Timeslot;
        requestTimeslot!: Timeslot;
}

export class Timeslot {
        start!: string;
        end!: string;
}

export class Calendar {
        year!: number;
        calId!: string;
        desc!: string;
        isLeapYear!: boolean;
        businessHours!: { [key: number]: DowBusinessHour; };
        overridingBusinessHours!: { [key: string]: OverridingBusinessHour; };
        daytimeSavingFrom!: string;
        daytimeSavingTo!: string;
        timeZoneId!: string;
        standardWorkingHoursOfDay!: number;
        holidays!: Holiday[];
        days!: Day[];
}

export class Day {
        date!: string;
        businessTimeFrom!: string;
        businessTimeTo!: string;
        dow!: number;
        isBusinessDay!: boolean;
        isDayLightSaving!: boolean;
        holiday!: Holiday;
        isOverridingWorkingHour!: boolean;
}

export class Holiday {
        ruleId!: string;
        name!: string;
        day!: string;
}

export class DowBusinessHour {
        dayOfWeek!: number;
        businessTimeFrom!: string;
        businessTimeTo!: string;
}


export class OverridingBusinessHour {
        day!: string;
        businessTimeFrom!: string;
        businessTimeTo!: string;
}

export class Element {
        error!: string;
        type!: string;
        text!: string;
}

export class User {
        userId!: string;
        person!: Person;
        orgId!: string;
        token!: string;
        password!: string; 
        status: string = "ACTIVE";
        //should be = []; after role configure
        roles: Role[] = [];
}

export class RegistrationForm {
        user!: User ;
        organization!: Organization;
}

export class Role {
        roleId: string = "";
        description: string = "";
}

export class Organization {
        orgId!: string;
        orgName! : string
        address! : string;
        category! : string;
        firstContactPerson!: Person;
        secondContactPerson!: Person;
}
    

export class Person {
        personId!: string;
        contact!: Contact;
        title!: string;
        solute!: string;
        firstName!: string;
        lastName!: string;
        position!: string;
}

export class Contact {
        contactId!: string;
        mailingAddress!: string;
        email!: string;
        primaryPhone!: string;
        secondaryPhone!: string;
        otherInfo!: string;
}

export class ApiError {
        errStatus!: string;
        timestamp!: Date;
        errCode !: string;
        errMessage !: string;

        subErrors !: string[];

        public static isError(err: ApiError) : boolean {
                if(err.errStatus && (err.timestamp || err.errCode)) {
                        return true;
                }
                return false;
        } 
}

export class RuleEditData {
        expression: string;
        title: string;
        passedTest: boolean = false;
        constructor(expression: string, title: string) {
                this.expression = expression;
                this.title = title;
        }
}

export class RuleExprTestResult {
        name: string = "";
        year?: number;
        success: boolean = true;
        ruleDates: String[] = [];
        ruleExprError?: RuleExprError;
        testDate: Date = new Date();
}

export class RuleExpr {
        name?: string;
        expr?: string;
}

export class CalendarAdminInstTestResult {
        success: boolean = true;
        year?: number;
        calendar?: Calendar;
        ruleExprErrors: RuleExprError[] = [];
        testDate: Date = new Date();
        updatedBusCalOwnership: BusinessCalendarOwnership | undefined;
}

export class RuleExprError {
        name: string = "";
        ruleType?: string;
        exprName?: string;
        elementErrors: Element[] = [];
}