import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MOCK_USER } from '../../../shared/constants/mock-data';
import { Event, ScheduleItem, SuggestedEvent } from '../models/event.model';
import { EventService } from '../services/event.service';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './events.component.html'
})
export class EventsComponent implements OnInit {
  events: Event[] = [];
  user = MOCK_USER;

  isLoading = true;
  error: string | null = null;

  tabs = ['Explore', 'Going', 'Saved', 'Past'];
  activeTab = 'Explore';

  schedule: ScheduleItem[] = [];
  suggestedEvents: SuggestedEvent[] = [];

  constructor(private eventService: EventService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.isLoading = true;
    this.error = null;
    this.eventService.getAll().subscribe({
      next: (data: Event[]) => {
        this.events = data;
        this.buildSchedule(data);
        this.buildSuggestedEvents(data);
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load events:', err);
        this.error = 'Failed to load events. Please try again.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** Build the sidebar schedule from the first few ongoing/upcoming events */
  private buildSchedule(events: Event[]): void {
    const now = new Date();
    const relevant = events
      .filter(e => e.status === 'ONGOING' || e.status === 'UPCOMING')
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())
      .slice(0, 3);

    this.schedule = relevant.map(e => {
      const start = new Date(e.startDate);
      let time: string;
      if (e.status === 'ONGOING') {
        time = start.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
      } else {
        const diffDays = Math.ceil((start.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
        if (diffDays <= 1) {
          time = 'Tomorrow';
        } else {
          time = start.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        }
      }
      return {
        time,
        title: e.title,
        subtitle: e.status === 'ONGOING' ? undefined : start.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' }),
        status: e.status
      } as ScheduleItem;
    });
  }

  /** Build suggested events from upcoming events that are not featured */
  private buildSuggestedEvents(events: Event[]): void {
    this.suggestedEvents = events
      .filter(e => e.status === 'UPCOMING' && !e.isFeatured)
      .slice(0, 4)
      .map(e => ({
        id: e.id,
        title: e.title,
        startDate: this.formatDate(e.startDate),
        eventType: e.eventType,
        isFree: e.isFree ?? (e.price == null || e.price === 0),
        image: e.image,
        category: e.category,
        targetLevel: e.targetLevel
      }));
  }

  /** Returns a live event if one is happening now, otherwise a featured or nearest upcoming event */
  get featuredEvent(): Event | undefined {
    const now = new Date();

    // 1. Check for a currently live event (now is between startDate and endDate)
    const liveEvent = this.events.find(e => {
      const start = new Date(e.startDate);
      const end = e.endDate ? new Date(e.endDate) : (e.duration
        ? new Date(start.getTime() + e.duration * 60000)
        : new Date(start.getTime() + 60 * 60000)); // default 1h if no endDate/duration
      return now >= start && now <= end;
    });
    if (liveEvent) return liveEvent;

    // 2. Fall back to explicitly featured event
    const featured = this.events.find(e => e.isFeatured);
    if (featured) return featured;

    // 3. Fall back to the nearest upcoming event
    return this.events
      .filter(e => new Date(e.startDate) > now)
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())[0];
  }

  /** Check if an event is currently live based on system time */
  isEventLive(event: Event): boolean {
    const now = new Date();
    const start = new Date(event.startDate);
    const end = event.endDate ? new Date(event.endDate) : (event.duration
      ? new Date(start.getTime() + event.duration * 60000)
      : new Date(start.getTime() + 60 * 60000));
    return now >= start && now <= end;
  }

  get upcomingEvents() {
    const featured = this.featuredEvent;
    return this.events
      .filter(e => e.status === 'UPCOMING' && (!featured || e.id !== featured.id))
      .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime());
  }

  get completedEvents() {
    return this.events.filter(e => e.status === 'COMPLETED');
  }

  formatDate(dateStr: string): string {
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
    } catch {
      return dateStr;
    }
  }

  getDay(dateStr: string): number {
    try { return new Date(dateStr).getDate(); } catch { return 0; }
  }

  getMonth(dateStr: string): string {
    try { return new Date(dateStr).toLocaleDateString('en-US', { month: 'short' }).toUpperCase(); } catch { return ''; }
  }

  getDayOfWeek(dateStr: string): string {
    try { return new Date(dateStr).toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase(); } catch { return ''; }
  }
}
