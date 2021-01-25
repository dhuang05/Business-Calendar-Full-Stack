
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Routes, RouterModule, ExtraOptions } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LogoutComponent } from './auth/logout/logout.component'
import { ForgetPasswordComponent } from './auth/forget-password/forget-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { CalAdminComponent } from './cal-admin/cal-admin.component';
import { AuthGuard } from './auth-guard';
import { ApiTestComponent } from './cal-admin/api-test/api-test.component';
import { UserAdminComponent } from './cal-admin/user-admin/user-admin.component';
import { RegistrationComponent } from './auth/registration/register.component';

export const APP_ROUTES: Routes = [
    { path: '', redirectTo: 'cal-admin', pathMatch: 'full' },
    { path: 'cal-admin', component: CalAdminComponent, canActivate: [AuthGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'forgetpassword', component: ForgetPasswordComponent },
    { path: 'resetpassword', component: ResetPasswordComponent },
    { path: 'logout', component: LogoutComponent },
    { path: 'apis', component: ApiTestComponent, canActivate: [AuthGuard]  },
    { path: 'user-admin', component: UserAdminComponent, canActivate: [AuthGuard]  },
    { path: 'registration', component: RegistrationComponent },
];

const routerOptions: ExtraOptions = { 
    scrollPositionRestoration: 'enabled', 
    anchorScrolling: 'enabled', 
    scrollOffset: [0, 64], 
    useHash: true };

export const mainRouting = RouterModule.forRoot(APP_ROUTES, routerOptions);