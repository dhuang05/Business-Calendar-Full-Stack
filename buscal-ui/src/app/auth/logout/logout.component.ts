/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */


import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/model/cal-model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'cal-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit, OnDestroy {
  hasLogedIn: boolean = false;
  userSubscription: any;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute){
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {});
    if(this.authService.hasUser()) {
        this.hasLogedIn = true;
      }else {
        this.hasLogedIn = false;
    }
    this.userSubscription = this.authService.getUserEventEmitter()
      .subscribe((user: User) => {
        if(user != undefined ) {
          this.hasLogedIn = true;
        }else {
          this.hasLogedIn = false;
        }
      }
      );
  }

  ngOnDestroy(){
    if(this.userSubscription != null) {
      this.userSubscription.unsubscribe();
    }
  }

  logout(e:Event){
    if( this.authService.getUser()) {
      let userId = this.authService.getUser().userId; 
      this.authService.logout (userId);
    }

    this.router.navigate(["../login"]);
  }

  
}
