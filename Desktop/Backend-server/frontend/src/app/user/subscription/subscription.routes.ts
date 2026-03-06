import { Routes } from '@angular/router';

export const SUBSCRIPTION_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/subscriptions.component').then(m => m.SubscriptionsComponent)
  }
];
