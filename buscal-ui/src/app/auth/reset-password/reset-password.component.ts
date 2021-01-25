/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, NgModule, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router, Params, ActivatedRoute } from '@angular/router';
import { ApiError, LoginForm, User } from 'src/app/model/cal-model';
import { Util } from 'src/app/common/util';

@Component({
  selector: 'cal-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: [ './reset-password.component.scss' ]
})
export class ResetPasswordComponent implements OnInit {
  message: string = '';
  loginForm: LoginForm = new LoginForm();
  repeatNewpepassword = '';
  //
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;
  //
  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute){
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {});
  }

  resetpassword(e: Event){
    if(!this.canResubmit()) {
      return;
    }
    this.message = '';
    let isAllFilled: boolean = !this.isEmpty(this.loginForm.userId) && !this.isEmpty(this.loginForm.password) && !this.isEmpty(this.loginForm.newPassword) && !this.isEmpty(this.repeatNewpepassword);
    if(isAllFilled){
      if(this.loginForm.newPassword.trim() == this.repeatNewpepassword.trim()) {
        this.authService.resetpassword(this.loginForm).subscribe(resp => {
          let json = JSON.stringify(resp);
          let error: ApiError = JSON.parse(json);
          if(!ApiError.isError(error)) {
            let user: User = JSON.parse(json);
            this.authService.setUser(user);
            this.router.navigate(['cal-admin']);
            this.router.navigate(['login']);
          } else {
            this.message = error.errMessage;
          }
         },
         error => {
          this.message = Util.handleError(error);
         }
         );
    
      } else {
        this.message = 'New password and the Retype password matched.';
      }

    } else {
      this.message = 'User id, password and new password, retype password are all required.';
    }
  }

  

  toLogin(e: Event){
    this.router.navigate(["./login"]);
  }


  isEmpty(text: string){
    return text == null || text == undefined || text.trim().length == 0;
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
