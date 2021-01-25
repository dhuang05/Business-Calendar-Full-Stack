/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { Util } from 'src/app/common/util';
import { User} from 'src/app/model/cal-model';

@Component({
  selector: 'cal-admin-header',
  templateUrl: './admin-header.component.html',
  styleUrls: ['./admin-header.component.scss']
})
export class AdminHeaderComponent implements OnInit, OnDestroy {
  @Input() currentPageName: string = "";
  isUserHasSuperRole: boolean | undefined;
  isAdminRole : boolean | undefined;
  userInfoSubscription : any;

  public static CAL_ADMIN = "CAL_ADMIN";
  public static RESET_PASSWORD = "RESET_PASSWORD";
  public static API = "API";
  public static USER_ADMIN = "USER_ADMIN";



  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService){
  }

  ngOnInit() {
    this.isUserHasSuperRole =  this.authService.hasSupperRole();
    this.isAdminRole = this.authService.hasAdminRoles();
    this.userInfoSubscription = this.authService.getUserEventEmitter()
      .subscribe((user: User) => {
          this.isUserHasSuperRole =  this.authService.hasSupperRole();
          this.isAdminRole = this.authService.hasAdminRoles();
      });
  }

  ngOnDestroy(){
    if(this.userInfoSubscription != null) {
      this.userInfoSubscription.unsubscribe();
    }
  }

  isMyPage(myPageName: string){
    return Util.isEqual(this.currentPageName, myPageName);
  }

  gotToResetPassword(){
    if(this.authService.getUser() != undefined) {
      this.router.dispose();
      this.router.navigate(['resetpassword']);
    }
  }
  

  gotToCalAdmin(){
    this.router.dispose();
    this.router.navigate(['cal-admin']);
  }


  gotToApis(){
    this.router.dispose();
    this.router.navigate(['apis']);
  }

  gotToUserAdmin(){
    this.router.dispose();
    this.router.navigate(['user-admin']);
  
  }

}

