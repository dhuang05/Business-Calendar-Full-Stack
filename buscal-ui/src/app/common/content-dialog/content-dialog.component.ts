
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { CalErr } from '../common-model';


@Component({
  selector: 'cal-content-dialog',
  templateUrl: './content-dialog.component.html',
  styleUrls: ['./content-dialog.component.scss']
})
export class ContentDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ContentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: String
    ){
  }

  ngOnInit() {
  }

  ok() {
    this.dialogRef.close();
  }


}
