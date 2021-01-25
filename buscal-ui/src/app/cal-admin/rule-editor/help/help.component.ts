/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { CalErr } from 'src/app/common/common-model';
import { RuleEditData } from 'src/app/model/cal-model';


@Component({
  selector: 'cal-rule-editor-help',
  templateUrl: './help_en.component.html',
  styleUrls: ['./help.component.scss']
})
export class RuleEditorHelpComponent implements OnInit {
  @Input() language: string = "en";

  constructor(
    public dialogRef: MatDialogRef<RuleEditorHelpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: String
    ){
  }

  ngOnInit() {
  }

  ok() {
    this.dialogRef.close(this.data);
  }

}

