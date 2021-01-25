
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class HttpService {
  constructor(
    private httpClient: HttpClient,
    private router: Router, private route: ActivatedRoute,
  ) {}

  protected post(path: string, payload: any, ...params : string[]): Observable<any> {
    path = this.buildPath(path, params);
    const rtn = this.httpClient.post(path, payload);
    return rtn;
  }

  protected put(path: string, payload: any, ...params : string[]): Observable<any> {
    path = this.buildPath(path, params);
    const rtn = this.httpClient.put(path, payload);
    return rtn;
  }

  protected get(path: string, ...params: string[]): Observable<any> {
    path = this.buildPath(path, params);
    const rtn = this.httpClient.get(path);
    return rtn;
  }

  protected delete(path: string, ...params : string[]): Observable<any> {
    path = this.buildPath(path, params);
    const rtn = this.httpClient.delete(path);;
    return rtn;
  }

  protected patch(path: string, payload: any, ... params : string[]): Observable<any> {
    const rtn = this.httpClient.patch(path, payload);
    return rtn;
  }

  protected buildPath(path: string, params: string[]): string {
    if(params == null || params.length == 0) {
      return path;
    }
    path = path.trim() + "?"

    for (var i = 0; i < (params.length/2); i++) {  
      if(i > 0) {
        path += "&";
      }
      path += params[i * 2].trim() + "=" + params[2 * i + 1].trim(); 
    }  
    return encodeURI(path);
  }

  private handleError(e: any) {
    console.error('error in http service.', e);
    return e;
  }

}
