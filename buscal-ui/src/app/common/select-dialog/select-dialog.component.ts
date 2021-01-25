
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { CalErr } from '../common-model';
import { Util } from '../util';


@Component({
  selector: 'cal-select-dialog',
  templateUrl: './select-dialog.component.html',
  styleUrls: ['./select-dialog.component.scss']
})
export class SelectDialogComponent implements OnInit {
  
  constructor(
    public dialogRef: MatDialogRef<SelectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SelectData
    ){
  }

  ngOnInit() {
  }

  selected() {
    if(this.data.selectedValue && !Util.isEmpty(this.data.selectedValue)) {
      this.dialogRef.close(this.data.selectedValue);
    }
  }

  no() {
    this.dialogRef.close(undefined);
  }

 yes() {
    this.dialogRef.close(this.data.selectedValue);
  }


}

export class SelectData{
  title: string = "Please select";
  selectedValue: any;
  options: OptionData[] = [];
}

export class OptionData {
  name: string = "";
  value: any;
}
