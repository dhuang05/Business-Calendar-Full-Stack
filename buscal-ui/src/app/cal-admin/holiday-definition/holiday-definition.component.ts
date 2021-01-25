/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { map } from 'rxjs/operators';
import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiError, BusinessHour, DayRule, RuleEditData } from 'src/app/model/cal-model';
import { EventEmitter } from '@angular/core';
import { Output } from '@angular/core';
import { CalErr } from 'src/app/common/common-model';
import { MatDialog } from '@angular/material/dialog';
import { RuleEditorComponent } from '../rule-editor/rule-editor.component';
import { InfoDialogComponent } from 'src/app/common/info-dialog/info-dialog.component';
import { Util } from 'src/app/common/util';



@Component({
  selector: 'cal-holiday-definition',
  templateUrl: './holiday-definition.component.html',
  styleUrls: [ './holiday-definition.component.scss' ]
})
export class HoldayDefinitionComponent implements OnInit {
  errorInfo: CalErr| undefined;
  @Input() holidayRule: DayRule | undefined;
  passedTest: boolean = true;
  @Output() deleteEvent = new EventEmitter<DayRule>();
  @Output() changedEvent = new EventEmitter<boolean>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog){
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {});
    this.checkValue(false);
  }

  checkValue( isChanged: boolean) {
    this.errorInfo = undefined;
    let messages: string[] = [];
    if(this.isEmpty(this.holidayRule?.desc)) {
      messages.push("Holiday Name");
    }
    if(this.isEmpty(this.holidayRule?.expr)) {
      messages.push(" Rule expression");
    }
    if(messages.length > 0) {
      let errorInfo = new CalErr();
      errorInfo.errMsgs = messages;
      errorInfo.title = "Required:";
      this.errorInfo = errorInfo;
    } 
    if(isChanged) {
        this.contentChanged();
    }
  }
  
  openEditor() : void {
    if(Util.isEmpty(this.holidayRule?.desc)) {
      const dialogRef = this.dialog.open(InfoDialogComponent, {
        width: '250px',
        height: '200px',
        data: "Please input name before editing rule expression."
      });
    } else {
      const dialogRef = this.dialog.open(RuleEditorComponent, {
        width: '1000px',
        height: '620px',
        data: new RuleEditData(this.holidayRule?.expr as string, this.holidayRule?.desc as string)
      });
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The holiday expr editor was closed');
        let data: RuleEditData = result;
        if(data && this.holidayRule) {
          this.holidayRule.expr = data.expression;
          this.checkValue(true);
        }
      });
    }
  }

  contentChanged() {
    this.changedEvent.emit(true);
  }

  delete(){
    this.deleteEvent.emit(this.holidayRule);
  }

  deleteExpiryDate() {
    if(this.holidayRule) {
      this.holidayRule.expiredDate = undefined
    }

  }
  deleteEffectiveDate() {
    if(this.holidayRule) {
      this.holidayRule.effectiveDate = undefined
    }
  }

  test(){
    
  }

  isEmpty(text: string| undefined): boolean{
    return text == null || text == undefined || text.trim().length == 0;
  }

  ruleStyle() {
    return (this.passedTest? 'color:darkblue' :'color:darkred') + ";cursor: pointer";
  }
}
