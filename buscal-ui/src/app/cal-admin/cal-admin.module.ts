
/**
 * Copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

import { NgModule } from '@angular/core';


import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthGuard } from '../auth-guard';
import { CalAdminComponent } from './cal-admin.component';
import { WeeklyWorkingHourComponent } from './weekly-workinghour/weekly-workinghour.component';
import { AuthService } from '../auth/services/auth.service';
import { CalAdminService } from './services/cal_admin.service';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule} from '@angular/material/icon'
import { TextFieldModule } from '@angular/cdk/text-field';
import { SpecialWorkingHourComponent } from './special-workinghour/special-workinghour.component';
import { HoldayDefinitionComponent } from './holiday-definition/holiday-definition.component';
import { CalErr } from '../common/common-model';
import { ErrorComponent } from '../common/error/error.component';
import { LogoutComponent } from '../auth/logout/logout.component';
import { AuthModule } from '../auth/auth.module';
import { RuleEditorComponent } from './rule-editor/rule-editor.component';
import { RuleEditorHelpComponent } from './rule-editor/help/help.component';
import {MatTabsModule} from '@angular/material/tabs';
import { ExprTestViewComponent } from './rule-editor/expr-test-view/expr-test-view.component';
import {MatExpansionModule} from '@angular/material/expansion';
import { RouterModule } from '@angular/router';
import { CalendarViewComponent } from './calendar-view/calendar-view.component';
import {MatGridListModule} from '@angular/material/grid-list';
import { ApiTestComponent } from './api-test/api-test.component';
import { AdminHeaderComponent } from './admin-header/admin-header.component';
import { UserAdminComponent } from './user-admin/user-admin.component';
import { PersonComponent } from './user-admin/person/person.component';
import {MatRadioModule} from '@angular/material/radio';
import { SelectDialogComponent } from '../common/select-dialog/select-dialog.component';
import {ClipboardModule} from '@angular/cdk/clipboard';

@NgModule({
        declarations: [
            CalAdminComponent,
            WeeklyWorkingHourComponent,
            SpecialWorkingHourComponent,
            HoldayDefinitionComponent,
            ErrorComponent,
            RuleEditorComponent,
            RuleEditorHelpComponent,
            SelectDialogComponent,
            ExprTestViewComponent,
            CalendarViewComponent,
            ApiTestComponent,
            AdminHeaderComponent,
            CalAdminComponent,
            UserAdminComponent,
            PersonComponent,
        ],
        imports: [
            RouterModule,
            CommonModule,
            FormsModule,
            ReactiveFormsModule,
            MatInputModule,
            MatSelectModule,
            MatInputModule,
            MatIconModule,
            TextFieldModule,
            AuthModule,
            MatTabsModule,
            MatExpansionModule,
            MatGridListModule,
            MatIconModule,
            MatRadioModule,
            ClipboardModule,
        ],
        providers: [
            CalAdminService,
            AuthService
        ],
        exports: [
            
        ]
    }
)

export class CalAdminModule {}
