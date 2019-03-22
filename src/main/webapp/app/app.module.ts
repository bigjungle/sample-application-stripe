import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { Ng2Webstorage } from 'ngx-webstorage';
import { NgJhipsterModule } from 'ng-jhipster';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { StripeSharedModule } from 'app/shared';
import { StripeCoreModule } from 'app/core';
import { StripeAppRoutingModule } from './app-routing.module';
import { StripeHomeModule } from './home/home.module';
import { StripeAccountModule } from './account/account.module';
import { StripeEntityModule } from './entities/entity.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import * as moment from 'moment';
import { NgxStripeModule } from 'ngx-stripe';
import { StripePaymentsModule } from './payments/payments.module';
import 'hammerjs';
import { StripeAppPaypalPaymentsModule } from './paypal-payments/paypal-payments.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent, NavbarComponent, FooterComponent, PageRibbonComponent, ActiveMenuDirective, ErrorComponent } from './layouts';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: false,
            alertTimeout: 5000,
            i18nEnabled: true,
            defaultI18nLang: 'en'
        }),
        StripeSharedModule.forRoot(),
        StripeCoreModule,
        StripeHomeModule,
        StripeAccountModule,
        StripePaymentsModule,
        NgxStripeModule.forRoot('pk_test_xxxxxxxxxxxxxxxxxxxxxxxx'),
        StripeAppPaypalPaymentsModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
        StripeEntityModule,
        StripeAppRoutingModule
    ],
    declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class StripeAppModule {
    constructor(private dpConfig: NgbDatepickerConfig) {
        this.dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
    }
}
