/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { CalErr } from 'src/app/common/common-model';
import { ConfirmDialogComponent } from 'src/app/common/confirm-dialog/confirm-dialog.component';
import { ContentDialogComponent } from 'src/app/common/content-dialog/content-dialog.component';
import { InfoDialogComponent } from 'src/app/common/info-dialog/info-dialog.component';
import { OptionData, SelectData, SelectDialogComponent } from 'src/app/common/select-dialog/select-dialog.component';
import { Util } from 'src/app/common/util';
import { RuleEditData, RuleExpr, RuleExprTestResult } from 'src/app/model/cal-model';
import { CalAdminService } from '../services/cal_admin.service';
import { ExprTestViewComponent } from './expr-test-view/expr-test-view.component';
import { RuleEditorHelpComponent } from './help/help.component';

@Component({
  selector: 'cal-rule-editor',
  templateUrl: './rule-editor.component.html',
  styleUrls: ['./rule-editor.component.scss']
})
export class RuleEditorComponent implements OnInit {
  exprTestResult: RuleExprTestResult | undefined;
  canSave = false;
  @Output() testEvent = new EventEmitter<boolean>();
  selectedYear: number;
  years:number[] ;
  optionDatas: OptionData[] = [];
  oringinalExpr: string; 
  functionNames: string[] = [];
  samples: Sample[] = [];
  previousExpr: string | undefined;
  //
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;

  constructor(
    private router: Router,
    private calAdminService: CalAdminService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<RuleEditorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RuleEditData)
    {
      this.oringinalExpr = data.expression;
      let date = new Date();
      this.selectedYear = date.getFullYear();
      this.years = [];
      for(let i =0; i< 10; i++) {
        this.years.push(this.selectedYear + i);
        let optionData = new OptionData();
        optionData.name = "" + (this.selectedYear + i);
        optionData.value = this.selectedYear + i;
        this.optionDatas.push(optionData);
      }
  }

  ngOnInit() {
    //this.allAddOnFunctions = "";
    this.calAdminService.getAllAddOnFunctions().subscribe(resp => {
      let json = JSON.stringify(resp);
      //console.log("json: " + json);
      this.functionNames = JSON.parse(json) as string[];
      //for (let func of functionNames) {
      //  this.allAddOnFunctions += func + "\n";
      //}
      
     });

     this.makeSamples();
  }

  isEdited(): boolean {
    let orgEnpty = this.isEmpty(this.oringinalExpr);
    let newEmpty = this.isEmpty(this.data.expression);
    if(orgEnpty && newEmpty) {
      return false;

    } else if ((!orgEnpty && newEmpty) || (orgEnpty && !newEmpty) ) {
      return true;

    } else if(this.oringinalExpr.trim() != this.data.expression.trim()) {
      return true;
      
    } else {
      return false;
    }
  }

  testRule() {
    this.selectedYear = new Date().getFullYear();
    let selectData = new SelectData(); 
    selectData.options = this.optionDatas;
    selectData.selectedValue = this.selectedYear;
    selectData.title = "Please select a year to test expression.";
    const dialogRef = this.dialog.open(SelectDialogComponent, {
      width: '300px',
      height: '220px',
      data: selectData
    });

    dialogRef.afterClosed().subscribe(result => {
       if(result) {
         this.selectedYear = result as number;
        let ruleExpr = new RuleExpr();
        ruleExpr.expr = this.data.expression;
        ruleExpr.name = this.data.title;
        this.calAdminService.testCalendarRuleExpr(ruleExpr, this.selectedYear).subscribe(resp => {
          let json = JSON.stringify(resp);
          //console.log("json: " + json);
          this.exprTestResult = JSON.parse(json);
          if(this.exprTestResult?.success) {
            this.canSave = true;
          }

          const dialogRef = this.dialog.open(ExprTestViewComponent, {
            width: '400px',
            height: '550px',
            data: this.exprTestResult
          });
      
          dialogRef.afterClosed().subscribe(result => {
             if(result == true) {
              this.dialogRef.close();
             }
          });
          //

         });
       }
    });
  }

  cancel() {
    if(this.canSave && this.isEdited()) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '300px',
        height: '200px',
        data: "The rule expression has been edited, Are you sure to cancel without update?"
      });
  
      dialogRef.afterClosed().subscribe(result => {
         if(result == true) {
          this.dialogRef.close();
         }
      });
    } else {
      this.dialogRef.close();
    }
  }

  help() {
    const dialogRef = this.dialog.open(RuleEditorHelpComponent, {
      width: '600px',
      height: '550px',
      //data: "The rule expression has been edited, Are you sure to cancel without update?"
    });
    
  }

  saveRule() {
    if(this.exprTestResult != undefined && this.exprTestResult.success) {
      this.dialogRef.close(this.data);
    }
  }

  copyToRule(expr: string) {
    if(expr && !Util.isEmpty(expr)) {
      if(!this.data.expression) {
        this.previousExpr = this.data.expression;
        this.data.expression = expr;
        return;
      }
      if(expr.trim().endsWith(";") && !Util.isEmpty(this.data.expression.trim())) {
        const dialogRef = this.dialog.open(InfoDialogComponent, {
          width: '250px',
          height: '200px',
          data: "The range definition should be in the beginning of the rule expression."
        });
        return;
      }
      let temp  = this.data.expression;
      if(!temp.trim().endsWith("and") && !temp.trim().endsWith("or") ) {
        temp += " and";
      }
      temp += " " + expr;
      this.previousExpr = this.data.expression;
      this.data.expression = temp;
    }
  }

  undoPast() {
    if(this.previousExpr) {
       this.data.expression = this.previousExpr;
       this.previousExpr = undefined;
    }
  }

  changeExpr(e: Event) {
    this.canSave = false;
  }

  isEmpty(text: string| undefined): boolean{
    return text == null || text == undefined || text.trim().length == 0;
  }

  
 makeSamples() {
  let samples: Sample[] = [];
  //samples.push(new Sample("","Leading should follow by date expression, and end by ';'"));
  samples.push(new Sample("From November/1 To Nov/15th;","Leading"));
  samples.push(new Sample("Between Jan 1th and Jan 09;","Leading"));
  samples.push(new Sample("","Month, Day, Day of week"));
  samples.push(new Sample("December 25, 2021","or 2021 Dec/25"));
  samples.push(new Sample("Sept 5th Friday","Sept/5th and Friday"));
  samples.push(new Sample("5th Fri ","5th of each month and Friday"));
  samples.push(new Sample("Dec and Fri (1,2)","1 or 2 days after"));
  samples.push(new Sample("5th","5th of each month"));
  samples.push(new Sample("May 10 (-1, 0, 1, 2, 3)","May/9...May/13"));
  samples.push(new Sample("","xth day of week in month"));
  samples.push(new Sample("The 3rd Friday in October","try it"));
  samples.push(new Sample("4th Mon of every month","each/all"));
  samples.push(new Sample("2th Mon in all months","every/each"));
  samples.push(new Sample("","Last Day of week in month"));
  samples.push(new Sample("3rd last Friday of June","Reverse"));
  samples.push(new Sample("The last Wed of July","Reverse"));
  samples.push(new Sample("","Last dy of month"));
  samples.push(new Sample("Last day of February", "Reverse"));
  samples.push(new Sample("","day of week in year"));
  samples.push(new Sample("The 13rd Monday of the year","In year"));
  samples.push(new Sample("43rd Mon of year","in year"));
  samples.push(new Sample("","Others"));
  samples.push(new Sample("Fri and  not (Dec or Jan)","Complex"));
  samples.push(new Sample("May/24(0,-1,-2,-3) and (Mon or Thu)",""));
  samples.push(new Sample("Chinese 5/5","Dragon boat day"));
  samples.push(new Sample("Chinese 01/01","Chinese new year"));
  

  this.samples = samples;

 }

}

export class Sample{
  expr: string;
  desc: string = "";
  constructor (
    expr:string,
    desc: string
  ){
    this.expr = expr;
    this.desc = desc;
  }
}