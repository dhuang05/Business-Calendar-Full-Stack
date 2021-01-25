/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */


import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { Util } from 'src/app/common/util';
import { Person} from 'src/app/model/cal-model';


@Component({
  selector: 'cal-person',
  templateUrl: './person.component.html',
  styleUrls: ['./person.component.scss']
})
export class PersonComponent implements OnInit, OnDestroy {
  @Input() person: Person | undefined;
  @Input() required = false;
  
  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute){
  }

  ngOnInit() {
    
  }

  ngOnDestroy(){
    
  }

  logout(e:Event){
    
  }

  isEmpty(data: string | undefined | any) : boolean{
    return Util.isEmpty(data);
  }
  
}
