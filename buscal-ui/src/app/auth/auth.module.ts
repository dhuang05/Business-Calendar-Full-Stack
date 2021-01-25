/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { NgModule } from '@angular/core';

import { LoginComponent } from './login/login.component';
import { ForgetPasswordComponent } from './forget-password/forget-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { LogoutComponent } from './logout/logout.component';
import { AuthService } from './services/auth.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthGuard } from '../auth-guard';
import { RegistrationComponent } from './registration/register.component';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TextFieldModule } from '@angular/cdk/text-field';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTabsModule } from '@angular/material/tabs';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatRadioModule } from '@angular/material/radio';

@NgModule({
        declarations: [
            LoginComponent,
            ForgetPasswordComponent,
            ResetPasswordComponent,
            LogoutComponent,
            RegistrationComponent
        ],
        imports: [
            CommonModule,
            FormsModule,
            ReactiveFormsModule,
            MatInputModule,
            MatSelectModule,
            MatInputModule,
            MatInputModule,
            TextFieldModule,
            MatTabsModule,
            MatExpansionModule,
            MatGridListModule,
            MatInputModule,
            MatRadioModule,
        ],
        providers: [
            AuthService,
        ],
        exports: [
            ForgetPasswordComponent,
            ResetPasswordComponent,
            LogoutComponent,
            RegistrationComponent
        ]
    }
)

export class AuthModule {}
