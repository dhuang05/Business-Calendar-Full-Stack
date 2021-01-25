
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { CalErr } from '../common-model';


@Component({
  selector: 'cal-info-dialog',
  templateUrl: './info-dialog.component.html',
  styleUrls: ['./info-dialog.component.scss']
})
export class InfoDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<InfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: String
    ){
  }

  ngOnInit() {
  }

  ok() {
    this.dialogRef.close();
  }


}
