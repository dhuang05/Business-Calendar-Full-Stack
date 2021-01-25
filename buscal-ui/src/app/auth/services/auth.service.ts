/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { EventEmitter, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { Util } from 'src/app/common/util';
import { ApiError, BusinessCalendarOwnership, LoginForm, Organization, RegistrationForm, User} from 'src/app/model/cal-model';
import { HttpService } from 'src/app/service/http.service';


const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';


@Injectable({
    providedIn: 'root',
})
export class AuthService extends HttpService {
    private userEmitter: EventEmitter<User> = new EventEmitter();
    public static SUPER_ADMIN_ROLE: string = "SUPER_ADMIN_ROLE";
    public static ADMIN_ROLE: string = "ADMIN_ROLE";
    public static API_ROLE: string = "API_ROLE";
    public static TRIAL_ROLE = "TRIAL_ROLE";
    public static SUPER_USER: string = "SUPER_USER"; 

    public getUserEventEmitter() {
        return this.userEmitter;
    }

    public setUser(user: User) {
        this.storeUser(user);
        this.saveToken(user.token);
        this.userEmitter.emit(user);
    }

    public hasSupperRole(): boolean {
        return this.hasRoleOf(AuthService.SUPER_ADMIN_ROLE);
    }

    public hasTrialRole(): boolean {
        return this.hasRoleOf(AuthService.TRIAL_ROLE);
    }

    public hasAdminRoles(): boolean {
        return this.hasRoleOf(AuthService.ADMIN_ROLE) || this.hasRoleOf(AuthService.SUPER_ADMIN_ROLE);
    }

    public hasRoleOf(aRole: string): boolean {
        let user = this.fetchUser();  
        if (user && user.roles) {
            for (let role of user.roles) {
                if (role.roleId.trim().toUpperCase() == aRole.trim().toUpperCase()) {
                    return true;
                }
            }
        }

        return false;
    }

    public getUser(): User {
        return this.fetchUser() as User;
    }

    public hasUser(): boolean {
        return this.isLoggedIn();
    }

    public login(loginForm: LoginForm): Observable<any> {
        return super.post("api/admin/auth/user/login", loginForm);
    }

    public logout(userId: string) {
        let result = super.get("api/admin/user/logout/" + userId);
        window.sessionStorage.clear();
    }

    public resetpassword(loginForm: LoginForm) {
        return super.post("api/admin/auth/user/resetpassword", loginForm);
    }

    public forgetpassword(loginForm: LoginForm) {
        return super.post("api/admin/auth/user/forgetpassword", loginForm);
    }

    public saveUser(user: User) {
        let url = "api/admin/user";
        let result = super.post(url, user);
        return result;
    }

    public saveNewUser(user: User) {
        let url = "api/admin/newuser";
        let result = super.post(url, user);
        return result;
    }

    public registerUser(registerForm: RegistrationForm) {
        let url = "api/admin/auth/user/register";
        let result = super.post(url, registerForm);
        return result;
    }

    public saveOrganization(organization: Organization) {
        let url = "api/admin/organization";
        let result = super.post(url, organization);
        return result;
    }

    public findUsers(orgId: string, keyword: string) {
        let url = "api/admin/user";
        if(!Util.isEmpty(orgId) || !Util.isEmpty(keyword)) {
            url += "?" 
            let hasParam = false;
            if(!Util.isEmpty(orgId)) {
                if(hasParam) {
                    url += "&"
                }
                url += "orgId=" + orgId; 
                hasParam = true;
            }

            if(!Util.isEmpty(keyword)) {
                if(hasParam) {
                    url += "&"
                }
                url += "keyword=" + keyword; 
                hasParam = true;
            }
        }
        let result = super.get(url);
        return result;
    }

    public findOrganization(orgId: string) {
        let url = "api/admin/organization/" + orgId;
        let result = super.get(url);
        return result;
    }


    public findOrganizations(keyword: string | undefined) {
        let url = "api/admin/organizations";
        if(!Util.isEmpty(keyword)) {
            url += "?keyword=" + keyword; 

        }
        let result = super.get(url);
        return result;
    }


    public isLoggedIn(): boolean {
        return !Util.isEmpty(this.fetchUser());
    }

    public reloadUserCalendarOwnerships() {
        let user = this.fetchUser() as User;  
        return super.get("api/admin/user/businessCalendarOwnerships/" + user.orgId);
    }


    public saveToken(token: string): void {
        window.sessionStorage.removeItem(TOKEN_KEY);
        window.sessionStorage.setItem(TOKEN_KEY, token);
      }
    
      public getToken(): string| null {
        return window.sessionStorage.getItem(TOKEN_KEY);
      }
    
      public storeUser( user: User): void {
        window.sessionStorage.removeItem(USER_KEY);
        window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
      }
    
      public fetchUser(): User | undefined {
       let json = window.sessionStorage.getItem(USER_KEY);
       if(json) {
        let user =  JSON.parse(json) as User;
        return Object.setPrototypeOf(user, User.prototype);
       }
       return undefined;
      }

}
