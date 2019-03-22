import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'payment',
                loadChildren: './payment/payment.module#StripePaymentModule'
            },
            {
                path: 'paypal-completed-payments',
                loadChildren: './paypal-completed-payments/paypal-completed-payments.module#StripePaypalCompletedPaymentsModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StripeEntityModule {}
