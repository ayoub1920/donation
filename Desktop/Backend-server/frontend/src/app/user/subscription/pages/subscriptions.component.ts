import { Component, OnInit, OnDestroy } from '@angular/core';
import { finalize, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { SubscriptionPlan, PlanType } from '../models/subscription.model';
import { SubscriptionService, ServiceError } from '../services/subscription.service';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
  selector: 'app-subscriptions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subscriptions.component.html'
})
export class SubscriptionsComponent implements OnInit, OnDestroy {
  // UI State
  billingCycle: 'monthly' | 'yearly' = 'yearly';
  plans: SubscriptionPlan[] = [];
  isLoadingPlans = true;
  isBooking = false;
  activePlanId: number | null = null;
  
  // Feedback
  subscriptionMessage: string | null = null;
  subscriptionError: string | null = null;
  messageTimeout?: NodeJS.Timeout;

  // Plan display configuration
  readonly planDisplayNames: Record<PlanType, string> = {
    [PlanType.FREEMIUM]: 'Starter',
    [PlanType.STANDARD]: 'Scholar',
    [PlanType.PREMIUM]: 'Family'
  };

  readonly planFeatures: Record<PlanType, string[]> = {
    [PlanType.FREEMIUM]: [
      'Access to all basic courses',
      'Daily vocabulary quiz',
      'Community forum access'
    ],
    [PlanType.STANDARD]: [
      'Everything in Starter',
      'Unlimited hearts & lives',
      'Offline mode learning',
      'Personalized mistakes review',
      'No ads'
    ],
    [PlanType.PREMIUM]: [
      'Everything in Scholar',
      'Up to 6 premium accounts',
      'Family progress dashboard',
      'Parental controls'
    ]
  };

  readonly planCTA: Record<PlanType, string> = {
    [PlanType.FREEMIUM]: 'Current Plan',
    [PlanType.STANDARD]: 'Start 14-day Free Trial',
    [PlanType.PREMIUM]: 'Choose Family'
  };

  readonly premiumBenefits = [
    { label: 'Learn 2x faster', icon: '🚀' },
    { label: 'Learn anywhere offline', icon: '📥' },
    { label: 'Exclusive challenges', icon: '🏆' },
    { label: 'Ad-free environment', icon: '🛡️' }
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private subscriptionService: SubscriptionService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.clearMessageTimeout();
  }

  /**
   * Load all available subscription plans
   */
  private loadPlans(): void {
    this.isLoadingPlans = true;
    this.subscriptionError = null;

    this.subscriptionService.getAllPlans().pipe(
      takeUntil(this.destroy$),
      finalize(() => {
        this.isLoadingPlans = false;
      })
    ).subscribe({
      next: (plans) => {
        this.plans = plans;
      },
      error: (err: ServiceError) => {
        this.subscriptionError = err.message || 'Failed to load subscription plans.';
        console.error('Failed to load plans:', err);
      }
    });
  }

  /**
   * Handle plan selection and subscription
   */
  selectPlan(plan: SubscriptionPlan): void {
    // Clear previous messages
    this.clearMessageTimeout();
    this.subscriptionMessage = null;
    this.subscriptionError = null;

    // Check if user is authenticated
    const user = this.authService.currentUser;
    if (!user) {
      this.subscriptionError = 'You must be logged in to subscribe.';
      this.setMessageTimeout();
      return;
    }

    // Validate plan
    if (!plan.id) {
      this.subscriptionError = 'Invalid plan selected. Please try again.';
      this.setMessageTimeout();
      return;
    }

    // Check if it's the free plan
    if (this.subscriptionService.isFreePlan(plan)) {
      this.subscriptionMessage = `You're already on the ${this.planDisplayNames[plan.name]} plan.`;
      this.setMessageTimeout();
      return;
    }

    // Proceed with booking
    this.bookPlan(user.id, plan.id, plan);
  }

  /**
   * Book/subscribe to a plan
   */
  private bookPlan(userId: number, planId: number, plan: SubscriptionPlan): void {
    this.isBooking = true;
    this.activePlanId = planId;

    this.subscriptionService.bookPlan(userId, planId).pipe(
      takeUntil(this.destroy$),
      finalize(() => {
        this.isBooking = false;
        this.activePlanId = null;
      })
    ).subscribe({
      next: (subscription) => {
        this.subscriptionMessage = `Successfully subscribed to ${this.planDisplayNames[plan.name]}!`;
        this.setMessageTimeout();
        
        // Force refresh plans cache to show updated state if needed
        this.subscriptionService.clearCache();
      },
      error: (err: ServiceError) => {
        console.error('Booking failed:', err);
        this.subscriptionError = err.message || 'Failed to subscribe. Please try again.';
        this.setMessageTimeout();
      }
    });
  }

  /**
   * Calculate plan price based on billing cycle
   */
  getPrice(plan: SubscriptionPlan): number {
    if (this.billingCycle === 'monthly' && plan.price > 0) {
      return this.subscriptionService.getMonthlyPrice(plan.price);
    }
    return plan.price;
  }

  /**
   * Get price label (per month, per year, or free)
   */
  getPriceLabel(plan: SubscriptionPlan): string {
    if (plan.price === 0) return 'Forever free';
    return this.billingCycle === 'yearly' ? 'per year' : 'per month';
  }

  /**
   * Check if a plan is currently being processed
   */
  isPlanProcessing(plan: SubscriptionPlan): boolean {
    return this.isBooking && this.activePlanId === plan.id;
  }

  /**
   * Get CTA button text based on plan state
   */
  getCTAText(plan: SubscriptionPlan): string {
    if (this.isPlanProcessing(plan)) {
      return 'Processing...';
    }
    
    if (this.subscriptionService.isFreePlan(plan)) {
      return this.planCTA[plan.name];
    }
    
    return this.planCTA[plan.name];
  }

  /**
   * Check if CTA button should be disabled
   */
  isCTADisabled(plan: SubscriptionPlan): boolean {
    return this.isPlanProcessing(plan) || this.isLoadingPlans;
  }

  /**
   * Set timeout for auto-clearing messages
   */
  private setMessageTimeout(): void {
    this.clearMessageTimeout();
    this.messageTimeout = setTimeout(() => {
      this.subscriptionMessage = null;
      this.subscriptionError = null;
    }, 5000);
  }

  /**
   * Clear message timeout
   */
  private clearMessageTimeout(): void {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
      this.messageTimeout = undefined;
    }
  }

  /**
   * Get plan type for styling
   */
  getPlanClass(plan: SubscriptionPlan): string {
    switch (plan.name) {
      case PlanType.FREEMIUM:
        return 'border-gray-200 bg-gray-50';
      case PlanType.STANDARD:
        return 'border-blue-200 bg-blue-50';
      case PlanType.PREMIUM:
        return 'border-purple-200 bg-purple-50';
      default:
        return 'border-gray-200 bg-gray-50';
    }
  }

  /**
   * Get button class based on plan type
   */
  getButtonClass(plan: SubscriptionPlan): string {
    if (this.isPlanProcessing(plan)) {
      return 'opacity-50 cursor-not-allowed';
    }

    switch (plan.name) {
      case PlanType.FREEMIUM:
        return 'bg-gray-200 text-gray-700 hover:bg-gray-300';
      case PlanType.STANDARD:
        return 'bg-[#38a9f3] text-white hover:bg-[#0288d1]';
      case PlanType.PREMIUM:
        return 'bg-purple-500 text-white hover:bg-purple-600';
      default:
        return 'bg-gray-200 text-gray-700 hover:bg-gray-300';
    }
  }

  /**
   * Format price for display
   */
  formatPrice(price: number): string {
    return price.toFixed(2);
  }

  /**
   * Get savings percentage when choosing yearly
   */
  getYearlySavings(plan: SubscriptionPlan): number {
    if (plan.price === 0) return 0;
    const monthlyPrice = this.subscriptionService.getMonthlyPrice(plan.price);
    const yearlyMonthlyTotal = monthlyPrice * 12;
    const savings = ((yearlyMonthlyTotal - plan.price) / yearlyMonthlyTotal) * 100;
    return Math.round(savings);
  }
}
