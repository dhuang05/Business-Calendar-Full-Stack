/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import {Calendar, CalendarAdminInstTestResult, DowBusinessHour, Holiday, OverridingBusinessHour } from 'src/app/model/cal-model';

@Component({
  selector: 'cal-calendar-view',
  templateUrl: './calendar-view.component.html',
  styleUrls: ['./calendar-view.component.scss']
})
export class CalendarViewComponent implements OnInit, OnDestroy {
  message: string = '';
  @Input() testResult: CalendarAdminInstTestResult | undefined;

  dayOfWeeks: string[] = ["Sunday", "Monday", "Tuesday", "Wednesday", "Tuesday", "Friday", "Saturday"];

  monthMap: Map<number, string> = new Map();
  selectedYear: number = new Date().getFullYear();
  years: number[] = [];
  currentMonth = 0;

  year: Year | undefined;
  month: Month | undefined;

  constructor(
    private router: Router,
    private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.selectedYear = new Date().getFullYear();
    //
    this.monthMap.set(0, "January");
    this.monthMap.set(1, "February");
    this.monthMap.set(2, "March");
    this.monthMap.set(3, "April");
    this.monthMap.set(4, "May");
    this.monthMap.set(5, "June");
    this.monthMap.set(6, "July");
    this.monthMap.set(7, "August");
    this.monthMap.set(8, "September");
    this.monthMap.set(9, "October");
    this.monthMap.set(10, "November");
    this.monthMap.set(11, "December");

    this.makeCalendar();
  }

  ngOnDestroy() {

  }


  makeCalendar() {
    this.year = undefined;
    if (this.testResult) {
      let now = new Date();
      if(this.selectedYear == new Date().getFullYear()){
          this.currentMonth = now.getMonth();
      }

      let testResult = this.testResult as CalendarAdminInstTestResult;
      if (testResult.calendar) {
        let calendar: Calendar = testResult.calendar;
        //
        let businessHourMap: Map<number, DowBusinessHour> = new Map();
        if (calendar.businessHours) {
          for (let key in calendar.businessHours) {
            let value: DowBusinessHour = calendar.businessHours[key] as DowBusinessHour;
            value.businessTimeFrom = this.onlyMiniutes(value.businessTimeFrom);
            value.businessTimeTo = this.onlyMiniutes(value.businessTimeTo);
            businessHourMap.set(value.dayOfWeek, value);
          }
        }
        //
        let overridingBusinessHourMap: Map<string, OverridingBusinessHour> = new Map();
        if (calendar.overridingBusinessHours) {
          for (let key in calendar.overridingBusinessHours) {
            let value: OverridingBusinessHour = calendar.overridingBusinessHours[key] as OverridingBusinessHour;
            value.businessTimeFrom = this.onlyMiniutes(value.businessTimeFrom);
            value.businessTimeTo = this.onlyMiniutes(value.businessTimeTo);
            overridingBusinessHourMap.set(value.day, value);
          }
        }
        //
        let holidayMap: Map<String, Holiday> = new Map();
        if (calendar.holidays) {
          for (let holiday of calendar.holidays) {
            holidayMap.set(holiday.day, holiday);
          }
        }

        // make calendar
        let year = new Year();
        year.name = calendar.year;
        for (let i = 0; i < 12; i++) {
          let month = new Month();
          year.months.push(month);
          month.name = this.monthMap.get(i) as string;
          for (let j = 0; j < 31; j++) {
            let currentDate = new Date(calendar.year, i , j + 1);
            if (currentDate.getMonth() != i) {
              break;
            }
            let dow = currentDate.getDay();
            // make up leading empty
            if(j == 0) {
              for(let k = dow; k > 0; k--) {
                let day = new Day();
                month.days.push(day);
                day.isWorkingDay = false;
              }
            }
            if(dow == 0) {
              dow = 7;
            }
            let day = new Day();
            month.days.push(day);
            day.dayNum = j + 1;

            let monthText = ((i + 1 < 10) ? ('0' +  (i + 1)) : (i +1));
            let dayText = ((j + 1 < 10) ? ('0' +  (j + 1)) : (j +1));

            let dateKey: string = calendar.year + "-" + monthText + "-" + dayText;

            let businessHour = businessHourMap.get(dow);
            let overridingBusinessHour = overridingBusinessHourMap.get(dateKey);
            let holiday = holidayMap.get(dateKey);
            if(holiday) {
              day.holidayName = holiday.name;
              day.isWorkingDay = false;
            } else {
              if(overridingBusinessHour) {
                day.from = overridingBusinessHour.businessTimeFrom;
                day.to = overridingBusinessHour.businessTimeTo;
                day.isWorkingDay = true;
              } else {
                if(businessHour) {
                  day.from = businessHour.businessTimeFrom;
                  day.to = businessHour.businessTimeTo;
                  day.isWorkingDay = true;
                } else {
                  day.isWorkingDay = false;
                }
              }
            }
          }
        }
        this.year =  year;
        this.month = this.year?.months[0];
      }
    }
  }

  onlyMiniutes (timeText: string| undefined): string{
    if(!timeText){
      return "";
    } 
    timeText = timeText.substr(0, timeText.lastIndexOf(":"));
    return timeText;
  }

  moveToPreviuosMonth() {
    this.addDeltaToMonth(-1);
  }


  moveToNextMonth() {
    this.addDeltaToMonth(1);
  }

  goToMonth() {
    this.month = this.year?.months[this.currentMonth];
  }

  addDeltaToMonth(delta: number){
    let currentMonth = this.currentMonth + delta;
    if(currentMonth < 0) {
      currentMonth = 11;

    }
    if(currentMonth > 11) {
      currentMonth = 0;
    }
    this.currentMonth = currentMonth;
    this.month = this.year?.months[currentMonth];

  }

  //
  styleClass(day: Day): string {
    if (!day?.dayNum) {
      return "out-month";
    }
    if (day.isWorkingDay) {
      return "working-day";
    }
    return "not-working-day";
  }
  //
}



class Year {
  name: number | undefined;
  months: Month[] = [];
}

class Month {
  name: string = "";
  days: Day[] = [];
}

class Day {
  dayNum: number | undefined;
  isWorkingDay: boolean = true;
  from: string = "";
  to: string = "";
  holidayName: string = "";
}