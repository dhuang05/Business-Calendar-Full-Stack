/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { map } from 'rxjs/operators';
import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ApiError, LoginForm, User } from 'src/app/model/cal-model';
import { Util } from 'src/app/common/util';



@Component({
  selector: 'cal-login',
  templateUrl: './login.component.html',
  styleUrls: [ './login.component.scss' ]
})
export class LoginComponent implements OnInit {
  loginForm: LoginForm = new LoginForm();
  message: string = '';
  loginCount = 0;
  //
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;



  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute){
  }


  ngOnInit() {
    this.route.queryParams.subscribe(params => {});
  }

  login() {
    if(!this.canResubmit()) {
      return;
    }
    if(!this.isEmpty(this.loginForm.password) && !this.isEmpty(this.loginForm.userId)) {
      this.message = '';
      this.authService.login (this.loginForm).subscribe(resp => {
        let json = JSON.stringify(resp);
        //console.log(json);
        let error: ApiError = JSON.parse(json);
        if(!ApiError.isError(error)) {
          let user: User = JSON.parse(json);
          this.authService.setUser(user);
          this.router.dispose();
          this.router.navigate(['cal-admin']);
        } else {
          this.message = error.errMessage;
          this.loginCount++;
          if(this.loginCount > 3) {
            this.router.dispose();
            this.router.navigate(['forgetpassword']);
            this.loginCount = 0;
          }
        }
       },
       error => {
        this.message = Util.handleError(error);
       });
  
    } else  {
      this.message = "Please input user id and password";
    }
  }
  
  goToRegistration() {
    this.router.dispose();
    this.router.navigate(['registration']);
  }

  isEmpty(text: string| undefined): boolean{
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
