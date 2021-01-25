
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
//
import { HttpService } from './service/http.service';
//
import { AppComponent } from './app.component';
import { AuthModule } from './auth/auth.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth/services/auth.service';
import { mainRouting } from './app.routing';
import { AuthGuard } from './auth-guard';
import { MatInputModule} from '@angular/material/input';
import { MatSelectModule} from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { CalAdminModule } from './cal-admin/cal-admin.module';
import { MatDialogModule} from '@angular/material/dialog';
import {MatTabsModule} from '@angular/material/tabs';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatRadioModule} from '@angular/material/radio';
import {ClipboardModule} from '@angular/cdk/clipboard';
import { authInterceptorProviders } from './auth/services/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AuthModule,    
    HttpClientModule,
    RouterModule,
    FormsModule,
    CommonModule,
    ReactiveFormsModule,
    mainRouting,
    BrowserAnimationsModule,
    CalAdminModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
    MatGridListModule,
    MatRadioModule,
    ClipboardModule,
  ],
  providers: [
    HttpService,
    AuthService,
    AuthGuard,
    authInterceptorProviders
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
