/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { map } from 'rxjs/operators';
import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiError, BusinessCalendarOwnership, BusinessHour, CalendarAdminInstTestResult, CalendarInst, DayRule, User } from '../model/cal-model';
import { CalAdminService } from './services/cal_admin.service';
import { AuthService } from '../auth/services/auth.service';
import { Util } from '../common/util';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../common/confirm-dialog/confirm-dialog.component';
import { InfoDialogComponent } from '../common/info-dialog/info-dialog.component';
import { Observable } from 'rxjs';
import { ConstDataSet } from '../model/data-set';
import { utils } from 'protractor';
import { OptionData, SelectData, SelectDialogComponent } from '../common/select-dialog/select-dialog.component';
import { getMatFormFieldPlaceholderConflictError } from '@angular/material/form-field';

@Component({
  selector: 'cal-cal-admin',
  templateUrl: './cal-admin.component.html',
  styleUrls: [ './cal-admin.component.scss' ]
})
export class CalAdminComponent implements OnInit, OnDestroy {
  message: string = '';
  businessCalendarOwnerships!: BusinessCalendarOwnership[] | undefined;
  userAccessableCalendars: OptionData[] = [];
  NUM_DOW_MAP: Map<number, string> = ConstDataSet.numDowMap();
  DOW_NUM_MAP: Map<string, number>  = ConstDataSet.dowNumMap();
  timezones: string[] = ConstDataSet.timezones;
//
  userSubscription: any;
  isUserHasSuperRole = false;
  hasTrialRole = false;
  //
  selectedBusinessCalendarOwnership!: BusinessCalendarOwnership | undefined;
  selectedCalendarInst: CalendarInst | undefined;
  //temp only
  selectedWeeklyBusinessHours: BusinessHour[] = [];
  selectedSpecialBusinessHours: BusinessHour[] = [];
  specialBusinessHourChunks: BusinessHour[][] = [];
  holidayChunks: DayRule[][] = [];
  isContentChanged: boolean = false;
  backupCalendarInst: CalendarInst | undefined;
  selectedYear: number = new Date().getFullYear();
  testResult: CalendarAdminInstTestResult | undefined;
  //
  edited: boolean = false;
  
  dialogRef: any;
  years: number[] = [];
  step = 0;
  //
  calFilter: string = "";
  //
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;

  screenWidth: any;
  screenHeight: any;
  
  constructor(
    private router: Router,
    private calAdminService: CalAdminService,
    private authService: AuthService,
    private route: ActivatedRoute,
    public dialog: MatDialog){
      //

  }

  ngOnInit() {
    //
    this.screenWidth = window.innerWidth;
    this.screenHeight = window.innerHeight;
    //
    this.businessCalendarOwnerships = undefined;
    this.calFilter = "";
    if(this.authService.getUser() == undefined) {
      this.router.navigate(['login']);
      return;
    }
    this.loadCalendarOwnerships();

    this.isUserHasSuperRole =  this.authService.hasSupperRole();
    this.hasTrialRole = this.authService.hasTrialRole();
    this.userSubscription = this.authService.getUserEventEmitter()
      .subscribe((user: User) => {
        if(user != undefined) {
          this.isUserHasSuperRole =  this.authService.hasSupperRole();
          this.hasTrialRole = this.authService.hasTrialRole();
          this.calFilter = "";
        } else {
          this.isUserHasSuperRole =  false;
          this.hasTrialRole = false;
        }
      });

    this.years = [];
    let selectedYear = new Date().getFullYear();
    for (let i = 0; i < 10; i++) {
      this.years.push(this.selectedYear + i);
    }
    this.selectedYear = selectedYear;
    this.step = 0;
    //
    this.fetchUserAccessibleTemplate();
  }

canCreateNew() : boolean {
  this.hasTrialRole = this.authService.hasTrialRole();
  if(!this.hasTrialRole) {
    return true;
  } else {
    if(this.businessCalendarOwnerships && this.businessCalendarOwnerships.length > 0) {
      return false;
    } else {
      return true;
    }
  }
}

loadCalendarOwnerships() {
  this.authService.reloadUserCalendarOwnerships().subscribe(resp => {
    let json = JSON.stringify(resp);
    let error: ApiError = JSON.parse(json);
    if (!ApiError.isError(error)) {
        let ownerships: BusinessCalendarOwnership[] = JSON.parse(json);
        if (ownerships) {
            this.businessCalendarOwnerships = ownerships;
        }
    }
});
}

ngOnDestroy(){
    if(this.userSubscription != null) {
      this.userSubscription.unsubscribe();
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event :Event) {
    this.screenWidth = window.innerWidth;
    this.screenHeight = window.innerHeight;
  }
  
  fetchCalendarInstance(businessCalendarOwnership: BusinessCalendarOwnership) {
    let canLoadNew = true;
    if(this.isContentChanged) {
      this.dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '320px',
        height: '210px',
        data: "The calendar content has been edited, Are you sure to reload without persistence to backend?"
      });
  
      this.dialogRef.afterClosed().subscribe((result: boolean) => {
         if(result == true) {
          this.dialogRef.close();
          this.reload(businessCalendarOwnership);
         }
      });
    
    } else {
      this.reload(businessCalendarOwnership);
    }
  }

  private reload(businessCalendarOwnership: BusinessCalendarOwnership) {
    if(!this.canResubmit()) {
      return;
    }
    this.message = "";
    this.clearSelectd();
    this.calAdminService.fetchCalendarInst(businessCalendarOwnership.calId).subscribe(resp => {
      let json = JSON.stringify(resp);
      //console.log("json: " + json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
        this.selectedCalendarInst = JSON.parse(json);
        this.selectedBusinessCalendarOwnership = businessCalendarOwnership;
        this.sortCalendar(this.selectedCalendarInst as CalendarInst);
        this.backupCalendarInst = Util.copy(this.selectedCalendarInst);
      } else {
        this.message = error.errMessage;
      }
     },
     error => {
      this.message = Util.handleError(error);
     });
  }

  sortCalendar(selectedCalendarInst: CalendarInst) {
    this.selectedCalendarInst = selectedCalendarInst;
    this.sortOpenHours(this.selectedCalendarInst as CalendarInst);
    this.sortHolidys(this.selectedCalendarInst as CalendarInst);
  }

  sortHolidys(selectedCalendarInst: CalendarInst) {
    if(selectedCalendarInst.holidayRules) {
      this.holidayChunks = this.chunks(selectedCalendarInst.holidayRules, this.ruleNumPerRow()) as DayRule[][];
    }
  }

  sortOpenHours(selectedCalendarInst: CalendarInst) {
    let weeklyBusinessHours: BusinessHour[] = [];
    let specialBusinessHours: BusinessHour[] = [];
    for (let [key, value] of this.NUM_DOW_MAP) {
      let businessHour = new BusinessHour();
      businessHour.desc = value;
      businessHour.dayExpr = value;
      weeklyBusinessHours.push(businessHour);
    }
    for(let businessHour of selectedCalendarInst.businessHours) {
      if(businessHour.overriding) {
        specialBusinessHours.push(businessHour);
      }else{
        weeklyBusinessHours[this.DOW_NUM_MAP.get(businessHour.dayExpr.toUpperCase()) as number] = businessHour;
      }
    }
    this.selectedWeeklyBusinessHours = weeklyBusinessHours;
    this.selectedSpecialBusinessHours = specialBusinessHours;

    if(this.selectedSpecialBusinessHours) {
      this.specialBusinessHourChunks = this.chunks(this.selectedSpecialBusinessHours, this.ruleNumPerRow()) as BusinessHour[][];
    }
  }

  

  private makeEmpty(dow: number): BusinessHour {
    let businessHour = new BusinessHour();
    return businessHour;
  }

  newCalendarInstance(e: Event) {
    if(!this.canResubmit()) {
      return;
    }

    if(!this.canCreateNew()) {
      this.message = "Trial user can only have one calendar, Please contact admin to upgrade";
      const dialogRef = this.dialog.open(InfoDialogComponent, {
        width: '250px',
        height: '200px',
        data: this.message,
      });
      this.dialogRef.afterClosed().subscribe((result: boolean) => {
         this.dialogRef.close();
      });
      return;
    }

    if(!this.userAccessableCalendars || this.userAccessableCalendars.length == 0) {
      this.getTemplate(undefined);
      return;
    }
    let selectData = new SelectData(); 
    selectData.selectedValue = undefined;
    selectData.options = this.userAccessableCalendars;
    selectData.selectedValue = this.selectedYear;
    selectData.title = "Please select a calendar template.";
    const dialogRef = this.dialog.open(SelectDialogComponent, {
      width: '300px',
      height: '220px',
      data: selectData
    });
    dialogRef.afterClosed().subscribe(result => {
      let calId = undefined;
      if(result) {
        calId = result as string;
      } 
      this.getTemplate(calId);
    });
  }

    getTemplate(calId: string|undefined) {
        this.calAdminService.getCalendarInstTemplate (calId).subscribe(resp => {
      let json = JSON.stringify(resp);
      //console.log("json: " + json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
        let calIntTemplate: CalendarInst = JSON.parse(json);
        //
        this.selectedBusinessCalendarOwnership = new BusinessCalendarOwnership();
        this.selectedBusinessCalendarOwnership.ownerId = this.authService.getUser().orgId;
        this.selectedBusinessCalendarOwnership.status = "ACTIVE";
        this.selectedBusinessCalendarOwnership.calendarInst = calIntTemplate;
        this.selectedCalendarInst = this.selectedBusinessCalendarOwnership.calendarInst;
        //
        this.sortCalendar(this.selectedCalendarInst as CalendarInst);
        this.backupCalendarInst = Util.copy(this.selectedCalendarInst);
      } else {
        this.message = error.errMessage;
      }
     },
     error => {
      this.message = Util.handleError(error);
     });
  }

  fetchUserAccessibleTemplate(){
    this.calAdminService.getUserAccessibleCalednars().subscribe(resp => {
      let json = JSON.stringify(resp);
      //console.log("API json: " + json);
      let error: ApiError = JSON.parse(json);
      if(!ApiError.isError(error)) {
          let userAccessibleCalendarOwnerships =  JSON.parse(json) as BusinessCalendarOwnership[];
          let userAccessableCalendars: OptionData[] = [];
          for(let calOwner of userAccessibleCalendarOwnerships) {
            let optionData = new OptionData();
            optionData.name = calOwner.description;
            optionData.value = calOwner.calId;
            userAccessableCalendars.push(optionData);
          }
          this.userAccessableCalendars = userAccessableCalendars;

      } else {
        this.message = error.errMessage;
      }
     },
       error => {
        this.message = Util.handleError(error);
       });
  }

  deleteSpecialBusinessHour(businessHour: BusinessHour){
    if(this.selectedCalendarInst?.businessHours) {
      this.selectedCalendarInst?.businessHours.forEach( (item, index) => {
        if(item === businessHour) this.selectedCalendarInst?.businessHours.splice(index,1);
      });
    }
    this.sortOpenHours(this.selectedCalendarInst as CalendarInst);
    if(this.selectedSpecialBusinessHours) {
      this.specialBusinessHourChunks = this.chunks(this.selectedSpecialBusinessHours, this.ruleNumPerRow()) as BusinessHour[][];
    }
    this.isChanged();
  }

  addSpecialBusinessHour() {
    let businessHour = new BusinessHour();
    businessHour.overriding = true;
    this.selectedCalendarInst?.businessHours.push(businessHour);
    this.sortOpenHours(this.selectedCalendarInst as CalendarInst) ;
    this.isChanged();
  }

  deleteHolidayRule (holidayRule: DayRule){
    if(this.selectedCalendarInst?.holidayRules) {
      this.selectedCalendarInst?.holidayRules.forEach( (item, index) => {
        if(item === holidayRule) this.selectedCalendarInst?.holidayRules.splice(index,1);
      });
    }
    this.sortHolidys(this.selectedCalendarInst as CalendarInst) ;
    this.isChanged();
  }

  addHolidayRule() {
    let dayRule = new DayRule();
    this.selectedCalendarInst?.holidayRules.push(dayRule);
    this.sortHolidys(this.selectedCalendarInst as CalendarInst);
    this.isChanged();
  }

  clearSelectd(){
    this.selectedBusinessCalendarOwnership  = undefined;
    this.selectedCalendarInst = undefined;
    this.selectedWeeklyBusinessHours = [];
    this.selectedSpecialBusinessHours = [];
    this.holidayChunks = [];
    this.specialBusinessHourChunks = [];
    //
    this.testResult = undefined;
    this.isContentChanged  = false;
    this.backupCalendarInst = undefined;

    this.step = 0;
  }

  resetPassword(e: Event) {
    if(this.authService.getUser() != undefined) {
      this.router.dispose();
      this.router.navigate(['resetpassword']);
    }
  }
 
  chunks(arr:any[], n:number) : any[][]{
    let chunks: any[][] = [];
    for (let i = 0; i < arr.length; i += n) {
      chunks.push(arr.slice(i, i + n));
    }
    return chunks;
  }

  ruleNumPerRow() : number{
    if(!this.screenWidth) {
      return 4;
    }
    return Math.floor((this.screenWidth - 250 ) / 200.0);
    
  }

  isChanged() {
    this.isContentChanged = true || Util.isEqual(this.backupCalendarInst, this.selectedCalendarInst);
    this.message = "";
  }

  testAndSaveCalednar(toSave: boolean){
    this.selectedYear = new Date().getFullYear();
    this.doTestAndSave(toSave) ;
  }

  doTestAndSave(toSave: boolean)  {
    if(!this.canResubmit()) {
      return;
    }
  
    let validated = this.validate();
    let ownership: BusinessCalendarOwnership = Util.copy(this.selectedBusinessCalendarOwnership);
    ownership.calendarInst = this.selectedCalendarInst as CalendarInst;
    if(validated) {
      ownership.calendarInst = this.selectedCalendarInst as CalendarInst;
     let observable: Observable<any> | undefined = undefined;
     if(toSave) {
       observable = this.calAdminService.testAndSaveCalendarAdminInst(ownership, this.selectedYear);
     } else {
       observable = this.calAdminService.testCalendarAdminInst(ownership, this.selectedYear);
     }
     this.testResult = undefined;
      observable.subscribe(resp => {
        let json = JSON.stringify(resp);
        //console.log("json: " + json);
        let error: ApiError = JSON.parse(json);
        if(!ApiError.isError(error)) {
          this.testResult =  JSON.parse(json);
          if(toSave) {
            this.handlePostSaveResult(this.testResult);
          }
          this.handleReportErrors(this.testResult);
        } else {
          this.message = error.errMessage;
        }
       },
       error => {
        this.message = Util.handleError(error);
       });
    }
    //console.log( "After changed: " + JSON.stringify(this.selectedCalendarInst));
  }

  handleReportErrors(testResult: CalendarAdminInstTestResult| undefined) {
    if(testResult && testResult.ruleExprErrors && testResult.ruleExprErrors.length > 0) {
       let errors = "Errors detected in ";
       for(let exprErr of  testResult.ruleExprErrors) {
        errors += exprErr.exprName + "(" + exprErr + "); "
       }
       this.message = errors;
    }
  }

  doFilter(text : string): boolean {
    if(this.calFilter && Util.isEmpty(this.calFilter.trim())) {
      return true;
    }
    let regex = /[\s|,|;]{1,}/;
    let parts = this.calFilter.replace(regex, " ").split(" ");
    for(let part of parts) {
      if(text.toUpperCase().indexOf(part.toUpperCase().trim()) < 0) {
        return false;
      }
    }
    return true;
  }

  handlePostSaveResult(testResult: CalendarAdminInstTestResult| undefined){
    if(testResult) {
      this.isContentChanged = false;
      //
      if(testResult.updatedBusCalOwnership) {
        if(!this.businessCalendarOwnerships) {
          this.businessCalendarOwnerships = [];
        }
  
        let exsting = false;
        for(let i=0; i<this.businessCalendarOwnerships.length; i++ ) {
            if(this.businessCalendarOwnerships[i].calId == testResult.updatedBusCalOwnership.calId) {
              this.businessCalendarOwnerships[i] = testResult.updatedBusCalOwnership;
              exsting = true;
            }
        }
        if(!exsting) {
          this.businessCalendarOwnerships.push(testResult.updatedBusCalOwnership);
        }
      }

      const dialogRef = this.dialog.open(InfoDialogComponent, {
        width: '250px',
        height: '180px',
        data: "The calendar rules has been persisted to storage."
      });
      if(this.dialogRef) {
        this.dialogRef.afterClosed().subscribe((result: boolean) => {
          this.dialogRef.close();
        });
      }
     
    }
  }
  moveToPreviousYear() {
    this.selectedYear = Number(this.selectedYear) - 1;
    this.doTestAndSave(false);
  }

  moveToNextYear() {
    this.selectedYear = Number(this.selectedYear) + 1;
    this.doTestAndSave(false);
  }

  validate(): boolean {
    this.message = "";
    let hasError: boolean = false;
    //let missings: string[] = [];
    if(this.isUserHasSuperRole &&  Util.isEmpty(this.selectedCalendarInst?.calId)) {
      hasError = true;
      //missings
    }


    if(Util.isEmpty(this.selectedCalendarInst?.desc || this.selectedCalendarInst?.timeZone)) {
      hasError = true;
      //missings
    }

    let validatedBusinessHours: BusinessHour[] = [];
    for(let businessHour of this.selectedWeeklyBusinessHours) {
      if(!Util.isEmpty(businessHour.businessHourFrom) &&  !Util.isEmpty(businessHour.businessHourTo)){
        validatedBusinessHours.push(businessHour);
      }
    }
    for(let businessHour of this.selectedSpecialBusinessHours) {
      if(Util.isEmpty(businessHour.desc) || Util.isEmpty(businessHour.dayExpr)  || Util.isEmpty(businessHour.businessHourFrom)  || Util.isEmpty(businessHour.businessHourTo)){
        hasError = true;
      }else {
        validatedBusinessHours.push(businessHour);
      }
    }
    if(!hasError) {
      (this.selectedCalendarInst as CalendarInst).businessHours = validatedBusinessHours;
    }

    for (let holiday of (this.selectedCalendarInst as CalendarInst).holidayRules) {
      if(Util.isEmpty(holiday.desc) || Util.isEmpty(holiday.expr)){
        hasError = true;
      }
    }
    if(hasError) {
      this.message = "Some fields missing data, Please input all required fields."
    }

    return !hasError;
  }

  setStep (step: number) {
    this.step = step;
  }



  canResubmit(): boolean {
    if ((new Date().getTime() / 1000 - this.submitTime) > this.submitWait) {
      this.submitTime = new Date().getTime() / 1000;
      return true;
    } else {
      this.submitTime = new Date().getTime() / 1000;
      return false;
    }
  }

}
