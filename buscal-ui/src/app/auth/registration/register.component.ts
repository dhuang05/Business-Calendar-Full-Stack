/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */


import { map } from 'rxjs/operators';
import { Component, Input, NgModule, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ApiError, Contact, LoginForm, Organization, Person, RegistrationForm, User } from 'src/app/model/cal-model';
import { Util } from 'src/app/common/util';
import { validateHorizontalPosition } from '@angular/cdk/overlay';

@Component({
  selector: 'cal-register',
  templateUrl: './register.component.html',
  styleUrls: [ './register.component.scss' ]
})
export class RegistrationComponent implements OnInit {
  //
  message: string = '';
  user: User = new User();
  retypePassword: string | undefined;
  organization: Organization = new Organization();
  
  //
  submitTime = new Date().getTime() / 1000;
  submitWait = 1;
  userRoles: string[] = [];


  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute){
  }


  ngOnInit() {
    this.route.queryParams.subscribe(params => {});
    this.prepareUserAndOrganization();
  }

  register() {
    if(this.validate()) {
        this.organization.firstContactPerson = Util.copy(this.user.person);
        let registrationForm = new RegistrationForm();
        registrationForm.user = this.user;
        registrationForm.organization = this.organization;
        this.authService.registerUser(registrationForm).subscribe(resp => {
          let json = JSON.stringify(resp);
          //console.log(json);
          let error: ApiError = JSON.parse(json);
          if(!ApiError.isError(error)) {
            this.router.dispose();
              this.router.navigate(['login']);
          } else {
            this.message = error.errMessage;
          }
         },
         error => {
          this.message = Util.handleError(error);
         });
    }
  }

  validate(): boolean {
      let msg = "Missing Required field";
       if(Util.isEmpty(this.user.userId)) {
          this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.user.password)) {
        this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.retypePassword)) {
        this.message = msg;
        return false;
       }
       if(Util.isEmpty(this.user.person.firstName)) {
        this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.user.person.position)) {
        this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.user.person.lastName)) {
        this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.user.person.contact.email)) {
        this.message = msg;
          return false;
       }

       if(Util.isEmpty(this.organization.orgName)) {
        this.message = msg;
          return false;
       }
       if(Util.isEmpty(this.organization.address)) {
        this.message = msg;
          return false;
       }
        if(!Util.isValidatedEmail(this.user.person.contact.email)) {
          this.message = "Wrong email format.";
          return false;
        }

        if(!Util.isEqual(this.retypePassword, this.user.password)) {
          this.message = "password typing not match.";
          return false;
         }

         return true;
  }

  isEmpty(text: string| undefined) : boolean {
    return Util.isEmpty(text);
  }

  userIdValidate() {
    if(this.user && this.user.userId) {
      this.user.userId = this.user.userId.replace(/\s/g, "");
    }
  }

  goToLogin() {
    this.router.dispose();
    this.router.navigate(['login']);
  }

  prepareUserAndOrganization() {
    this.retypePassword = undefined;
    this.user = new User();
    if(this.user.roles) {
      this.user.roles = [];
      this.userRoles.push(AuthService.ADMIN_ROLE);
      this.userRoles.push(AuthService.API_ROLE);
      this.userRoles.push(AuthService.TRIAL_ROLE);
    }

    if(!this.user.person) {
      this.user.person = new Person();
    }
    if(!this.user.person.contact) {
      this.user.person.contact = new Contact();
    }
    this.organization = new Organization();
  }

}
