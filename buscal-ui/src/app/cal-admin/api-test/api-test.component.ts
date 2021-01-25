/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { ContentDialogComponent } from 'src/app/common/content-dialog/content-dialog.component';
import { InfoDialogComponent } from 'src/app/common/info-dialog/info-dialog.component';
import { Util } from 'src/app/common/util';
import { ApiError, BusinessCalendarOwnership} from 'src/app/model/cal-model';
import { ConstDataSet } from 'src/app/model/data-set';
import { CalAdminService } from '../services/cal_admin.service';

@Component({
  selector: 'cal-api-test',
  templateUrl: './api-test.component.html',
  styleUrls: ['./api-test.component.scss']
})
export class ApiTestComponent implements OnInit, OnDestroy {
  message: string | undefined;
  timezones: string[] = ConstDataSet.timezones;
  calendarOwnerships: BusinessCalendarOwnership[] | undefined;
  timeSlotRequest = new TimeSlotRequest();
  calInfoRequest = new CalInfoRequest();
  isUserHasSuperRole : boolean | undefined;
  requestTimeSlotError = "";
  requestCalInfoError = "";

  constructor(
    private router: Router,
    private authService: AuthService,
    private calAdminService: CalAdminService,
    private route: ActivatedRoute,
    public dialog: MatDialog){
  }

  ngOnInit() {
    this.isUserHasSuperRole =  this.authService.hasSupperRole();

    this.calAdminService.getUserAccessibleCalednars().subscribe(resp => {
      let json = JSON.stringify(resp);
      //console.log("API json: " + json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
        this.calendarOwnerships =  JSON.parse(json);
      } else {
        this.message = error.errMessage;
      }
     },
       error => {
        this.message = Util.handleError(error);
       });
  }

  ngOnDestroy(){
   
  }

  showCalInfos() {
    if(this.calendarOwnerships) {
      let calInfos: CalInfo[] = [];
      for(let calendarOwnership of this.calendarOwnerships) {
        let calInfo = new CalInfo();
        calInfo.calId = calendarOwnership.calId;
        calInfo.calName = calendarOwnership.description;
        calInfos.push(calInfo);
      }
      let json = JSON.stringify(calInfos, null, 2);
      let result =  "\n" + json as string;
      console.log("json=" + json );
        const dialogRef = this.dialog.open(ContentDialogComponent, {
          width: '500px',
          height: '500px',
          data: result,
        });
    }
    
  }

  getCalendarInfoUrl() : string {
    let url = "/api/calendar/inst/auth/info";
    if(this.calInfoRequest.calId) {
      url += "/" + this.calInfoRequest.calId;
    }else {
      url += "/{calId}";
    }
    if(this.calInfoRequest.year) {
      url += "/" + this.calInfoRequest.year;
    }else {
      url += "/{year}";
    }

    return url;
  } 
  
  getTimeslotUrl() : string{
    let url = "/api/calendar/auth/businesstime";
    if(this.timeSlotRequest.calId) {
      url += "/" + this.timeSlotRequest.calId;
    }else {
      url += "/{calId}";
    }
    //
    if(this.timeSlotRequest.getDuration()) {
      url += "/" + this.timeSlotRequest.getDuration();
    }else {
      url += "/{duration}";
    }
    if(this.timeSlotRequest.timezoneId || this.timeSlotRequest.sinceDateTime) {
      url += "?";
      let hasParam = false;
      if(this.timeSlotRequest.timezoneId) {
        if(hasParam) {
          url += "&";
        }
        url += "localZoneId=" + this.timeSlotRequest.timezoneId;
        hasParam = true;
      }

      if(this.timeSlotRequest.sinceDateTime) {
        if(hasParam) {
          url += "&";
        }
        url += "sinceDateTime=" + this.timeSlotRequest.sinceDateTime;
        hasParam = true;
      }
    }
    return url;

  }
  requestCalInfo() {
    let hasError = false;
    this.requestCalInfoError = "";
    if(!this.calInfoRequest.calId) {
      this.requestCalInfoError += "CalId required; ";
      hasError = true;
    }
    if(!this.calInfoRequest.year) {
      this.requestCalInfoError += "Year required; ";
      hasError = true;
    }
    if(hasError) {
      return;
    }
    
    this.calAdminService.genericGet(this.getCalendarInfoUrl()).subscribe(resp => {
      let json = JSON.stringify(resp, null, 2);
      //console.log(json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
        let result =  "\n" + json as string;
        const dialogRef = this.dialog.open(ContentDialogComponent, {
          width: '500px',
          height: '500px',
          data: result,
        });
      } else {
        this.requestCalInfoError = error.errMessage;
      }
     },
     error => {
      this.requestCalInfoError = Util.handleError(error);
     }
     );
    
  }

  requestTimeSlot() {
    let hasError = false;
    this.requestTimeSlotError = "";
    if(!this.timeSlotRequest.calId) {
      this.requestTimeSlotError += "CalId required; ";
      hasError = true;
    }
    if(!this.timeSlotRequest.getDuration()) {
      this.requestTimeSlotError += "Duration Required; ";
      hasError = true;
    }
    if(hasError) {
      return;
    }
    this.calAdminService.genericGet(this.getTimeslotUrl()).subscribe(resp => {
      let json = JSON.stringify(resp, null, 2);
      //console.log(json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
        let result =  "\n" + json as string;
        const dialogRef = this.dialog.open(ContentDialogComponent, {
          width: '500px',
          height: '500px',
          data: result,
        });
      } else {
        this.requestTimeSlotError = error.errMessage;
      }
     },
     error => {
      this.requestTimeSlotError = Util.handleError(error);
     }
     );
  }

}

class CalInfo {
  calId: string = "";
  calName: string = "";
}

class CalInfoRequest {
  calId: string | undefined;
  year: number | undefined;

  min = new Date().getFullYear();
  max = this.min + 20;
}

class TimeSlotRequest {
  calId: string | undefined;

  timezoneId: string | undefined;
  sinceDateTime: string | undefined;

  day: number| undefined;
  hour: number| undefined;
  minute: number| undefined;

  getDuration() : string | undefined{
    let duration = "";
    if(this.day) {
      duration += "d" + this.day;
    } 
    if(this.hour) {
      duration += "h" + this.hour;
    } 
    if(this.minute) {
      duration += "m" + this.minute;
    } 
    if(duration.trim() == "") {
      return undefined;
    }
    return duration;
  }

}
