import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IPaypalCompletedPayments } from 'app/shared/model/paypal-completed-payments.model';
import { AccountService } from 'app/core';
import { PaypalCompletedPaymentsService } from './paypal-completed-payments.service';

@Component({
    selector: 'jhi-paypal-completed-payments',
    templateUrl: './paypal-completed-payments.component.html'
})
export class PaypalCompletedPaymentsComponent implements OnInit, OnDestroy {
    paypalCompletedPayments: IPaypalCompletedPayments[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected paypalCompletedPaymentsService: PaypalCompletedPaymentsService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.paypalCompletedPaymentsService
            .query()
            .pipe(
                filter((res: HttpResponse<IPaypalCompletedPayments[]>) => res.ok),
                map((res: HttpResponse<IPaypalCompletedPayments[]>) => res.body)
            )
            .subscribe(
                (res: IPaypalCompletedPayments[]) => {
                    this.paypalCompletedPayments = res;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInPaypalCompletedPayments();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IPaypalCompletedPayments) {
        return item.id;
    }

    registerChangeInPaypalCompletedPayments() {
        this.eventSubscriber = this.eventManager.subscribe('paypalCompletedPaymentsListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
