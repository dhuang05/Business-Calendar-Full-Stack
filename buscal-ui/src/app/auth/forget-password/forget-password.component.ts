/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, NgModule, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiError, LoginForm } from 'src/app/model/cal-model';
import { Util } from 'src/app/common/util';


@Component({
  selector: 'cal-forget-password',
  templateUrl: './forget-password.component.html',
  styleUrls: ['./forget-password.component.scss']
})
export class ForgetPasswordComponent implements OnInit {
  loginForm: LoginForm = new LoginForm();
  errorMsg: string = '';
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute) {
  }

  ngOnInit() {
  }

  forgetpassword(e: Event) {
    if (!this.canSubmit()) {
      return;
    }
    this.errorMsg = '';
    let isValid = !this.isEmpty(this.loginForm.email) && !this.isEmpty(this.loginForm.email);
    if (isValid) {
      let atIndex = this.loginForm.email.trim().indexOf("@");
      let dotIndex = this.loginForm.email.trim().lastIndexOf(".");

      isValid = atIndex > 0 && dotIndex > 0 && dotIndex > atIndex;
      if (!isValid) {
        this.errorMsg = 'Email has wrong format';
      }
    } else {
      this.errorMsg = 'UserId and Email are both required';
    }

    if (isValid) {
      this.authService.forgetpassword(this.loginForm).subscribe(resp => {
        let json = JSON.stringify(resp);
        //console.log(json);
        let error: ApiError = JSON.parse(json);
        if (!ApiError.isError(error)) {
          this.errorMsg = 'Reset password link will be sent to your email, after information is verified.';
          //set not commit for a while.
          this.submitWait = 10000;
        } else {
          this.errorMsg = error.errMessage;
        }
      },
        error => {
          this.errorMsg = Util.handleError(error);
        });


    }
  }

  toLogin(e: Event) {
    this.router.navigate(["./login"]);
  }

  isEmpty(text: string): boolean {
    return text == null || text == undefined || text.trim().length == 0;
  }


  canSubmit(): boolean {
    if ((new Date().getTime() / 1000 - this.submitTime) > this.submitWait) {
      this.submitTime = new Date().getTime() / 1000;
      return true;
    } else {
      this.submitTime = new Date().getTime() / 1000;
      return false;
    }
  }
}
