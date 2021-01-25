
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { NgModule, ErrorHandler } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { mainRouting } from './app.routing';
import { RouteReuseStrategy, RouterModule } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { AuthModule } from './auth/auth.module';
import { ForgetPasswordComponent } from './auth/forget-password/forget-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { CalAdminComponent } from './cal-admin/cal-admin.component';
import { HttpService } from './service/http.service';
import { AuthService } from './auth/services/auth.service';

@NgModule({

  declarations: [
    AppComponent,
    LoginComponent,
    LoginComponent,
    ForgetPasswordComponent,
    ResetPasswordComponent,
    CalAdminComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule,
    mainRouting,
    AuthModule
  ],
  providers: [
    HttpService,
    AuthService,
  ],

  bootstrap: [AppComponent]
})
export class AppModule { }
