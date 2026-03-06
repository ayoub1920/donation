import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Event, EventType, EventStatus, TargetLevel } from '../../../user/event/models/event.model';
import { EventService } from '../../../user/event/services/event.service';

@Component({
  selector: 'app-admin-events',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './events.component.html'
})
export class AdminEventsComponent implements OnInit {
  events: Event[] = [];
  isLoading = true;
  error: string | null = null;

  tabs = ['All Events', 'Upcoming', 'Past', 'Drafts'];
  activeTab = 'All Events';

  selectedEvent: Event | null = null;

  // Modal state
  showModal = false;
  isEditing = false;
  isSaving = false;
  showDeleteConfirm = false;
  isDeleting = false;

  // Form model
  formData: Partial<Event> = {};
  formErrors: { [key: string]: string } = {};
  formSubmitted = false;

  get errorCount(): number {
    return Object.keys(this.formErrors).length;
  }

  // Dropdown options
  eventTypes: EventType[] = ['ONLINE', 'OUTDOOR', 'HYBRID'];
  eventStatuses: EventStatus[] = ['DRAFT', 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'];
  targetLevels: TargetLevel[] = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS'];

  constructor(
    private eventService: EventService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.isLoading = true;
    this.error = null;
    this.eventService.getAll().subscribe({
      next: (data: Event[]) => {
        this.events = data.sort((a, b) =>
          new Date(b.startDate).getTime() - new Date(a.startDate).getTime()
        );
        if (this.events.length > 0 && !this.selectedEvent) {
          this.selectedEvent = this.events[0];
        }
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to load events:', err);
        this.error = 'Failed to load events.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // --- Stats ---
  get stats() {
    const now = new Date();
    const upcoming = this.events.filter(e => e.status === 'UPCOMING' || new Date(e.startDate) > now);
    const totalAttendees = this.events.reduce((sum, e) => sum + (e.currentAttendees || 0), 0);
    const totalCapacity = this.events.reduce((sum, e) => sum + (e.maxAttendees || 0), 0);
    const avgAttendance = totalCapacity > 0 ? Math.round((totalAttendees / totalCapacity) * 100) : 0;

    return [
      { label: 'Upcoming Events', value: `${upcoming.length}`, sub: upcoming.length > 0 ? 'Active' : 'None planned', valueColor: 'text-[#38a9f3]' },
      { label: 'Total Registrations', value: `${totalAttendees.toLocaleString()}`, sub: `Across ${this.events.length} events`, valueColor: 'text-[#0f1419]' },
      { label: 'Avg Attendance', value: `${avgAttendance}%`, sub: avgAttendance >= 70 ? '⊘ Very High' : avgAttendance >= 40 ? '⊘ Moderate' : '⊘ Low', valueColor: 'text-[#0f1419]' },
      { label: 'Total Events', value: `${this.events.length}`, sub: 'All time', valueColor: 'text-[#0f1419]' }
    ];
  }

  // --- Tab filtering ---
  get filteredEvents(): Event[] {
    const now = new Date();
    switch (this.activeTab) {
      case 'Upcoming':
        return this.events.filter(e => e.status === 'UPCOMING' || (e.status !== 'COMPLETED' && e.status !== 'CANCELLED' && e.status !== 'DRAFT' && new Date(e.startDate) > now));
      case 'Past':
        return this.events.filter(e => e.status === 'COMPLETED' || (e.status !== 'DRAFT' && new Date(e.endDate || e.startDate) < now));
      case 'Drafts':
        return this.events.filter(e => e.status === 'DRAFT');
      default:
        return this.events;
    }
  }

  // --- Status helpers ---
  getStatusLabel(event: Event): string {
    const now = new Date();
    const start = new Date(event.startDate);
    const end = event.endDate ? new Date(event.endDate) : (event.duration
      ? new Date(start.getTime() + event.duration * 60000)
      : new Date(start.getTime() + 60 * 60000));

    if (event.status === 'DRAFT') return 'Draft';
    if (event.status === 'CANCELLED') return 'Cancelled';
    if (now >= start && now <= end) return 'Live Now';
    if (now > end || event.status === 'COMPLETED') return 'Past';
    return 'Upcoming';
  }

  getStatusColor(event: Event): string {
    const label = this.getStatusLabel(event);
    switch (label) {
      case 'Live Now': return 'bg-blue-100 text-blue-600';
      case 'Upcoming': return 'bg-green-100 text-green-600';
      case 'Past': return 'bg-gray-100 text-gray-500';
      case 'Draft': return 'bg-yellow-100 text-yellow-600';
      case 'Cancelled': return 'bg-red-100 text-red-500';
      default: return 'bg-gray-100 text-gray-500';
    }
  }

  getEventIcon(event: Event): string {
    switch (event.eventType) {
      case 'ONLINE': return '💻';
      case 'OUTDOOR': return '📍';
      case 'HYBRID': return '🔄';
      default: return '📅';
    }
  }

  // --- Selection ---
  selectEvent(event: Event): void {
    this.selectedEvent = event;
  }

  // --- CRUD: Create ---
  openCreateModal(): void {
    this.isEditing = false;
    this.formData = {
      title: '',
      description: '',
      image: '',
      category: '',
      startDate: '',
      endDate: '',
      duration: 60,
      timezone: 'Africa/Tunis',
      eventType: 'ONLINE',
      meetingLink: '',
      platform: '',
      location: '',
      maxAttendees: 100,
      status: 'UPCOMING',
      isFeatured: false,
      isPublic: true,
      hostName: '',
      contactEmail: '',
      targetLevel: 'ALL_LEVELS',
      skillFocus: '',
      price: 0,
      isFree: true,
      tags: []
    };
    this.formErrors = {};
    this.formSubmitted = false;
    this.showModal = true;
  }

  // --- CRUD: Edit ---
  openEditModal(event: Event): void {
    this.isEditing = true;
    this.formData = { ...event, tags: event.tags ? [...event.tags] : [] };
    this.formErrors = {};
    this.formSubmitted = false;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.formData = {};
    this.formErrors = {};
    this.formSubmitted = false;
  }

  validateForm(): boolean {
    this.formErrors = {};

    // Title
    if (!this.formData.title || !this.formData.title.trim()) {
      this.formErrors['title'] = 'Title is required.';
    } else if (this.formData.title.trim().length < 3) {
      this.formErrors['title'] = 'Title must be at least 3 characters.';
    }

    // Description
    if (!this.formData.description || !this.formData.description.trim()) {
      this.formErrors['description'] = 'Description is required.';
    } else if (this.formData.description.trim().length < 10) {
      this.formErrors['description'] = 'Description must be at least 10 characters.';
    }

    // Start date
    if (!this.formData.startDate) {
      this.formErrors['startDate'] = 'Start date is required.';
    }

    // End date > start date
    if (this.formData.startDate && this.formData.endDate) {
      if (new Date(this.formData.endDate) <= new Date(this.formData.startDate)) {
        this.formErrors['endDate'] = 'End date must be after start date.';
      }
    }

    // Duration
    if (this.formData.duration != null && this.formData.duration < 0) {
      this.formErrors['duration'] = 'Duration cannot be negative.';
    }

    // Max attendees
    if (this.formData.maxAttendees != null && this.formData.maxAttendees < 1) {
      this.formErrors['maxAttendees'] = 'Max attendees must be at least 1.';
    }

    // Price
    if (this.formData.price != null && this.formData.price < 0) {
      this.formErrors['price'] = 'Price cannot be negative.';
    }

    // Contact email format
    if (this.formData.contactEmail && this.formData.contactEmail.trim()) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.formData.contactEmail.trim())) {
        this.formErrors['contactEmail'] = 'Invalid email format.';
      }
    }

    this.cdr.markForCheck();
    return Object.keys(this.formErrors).length === 0;
  }

  saveEvent(): void {
    this.formSubmitted = true;
    if (!this.validateForm()) return;

    this.isSaving = true;
    const eventData = { ...this.formData } as Event;

    // Parse tags from a comma-separated string if needed
    if (typeof eventData.tags === 'string') {
      eventData.tags = (eventData.tags as unknown as string).split(',').map((t: string) => t.trim()).filter((t: string) => t);
    }

    const obs = this.isEditing
      ? this.eventService.update(eventData.id, eventData)
      : this.eventService.create(eventData);

    obs.subscribe({
      next: () => {
        this.showModal = false;
        this.isSaving = false;
        this.formData = {};
        this.selectedEvent = null;
        this.loadEvents();
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to save event:', err);
        this.isSaving = false;
        this.cdr.markForCheck();
      }
    });
  }

  // --- CRUD: Delete ---
  confirmDelete(): void {
    this.showDeleteConfirm = true;
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
  }

  deleteEvent(): void {
    if (!this.selectedEvent) return;
    this.isDeleting = true;
    this.eventService.delete(this.selectedEvent.id).subscribe({
      next: () => {
        this.showDeleteConfirm = false;
        this.isDeleting = false;
        this.selectedEvent = null;
        this.loadEvents();
        this.cdr.markForCheck();
      },
      error: (err: unknown) => {
        console.error('Failed to delete event:', err);
        this.isDeleting = false;
        this.cdr.markForCheck();
      }
    });
  }

  // --- Helpers ---
  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    try {
      return new Date(dateStr).toLocaleDateString('en-US', {
        month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit'
      });
    } catch { return dateStr; }
  }

  getCapacityPercent(event: Event): number {
    if (!event.maxAttendees) return 0;
    return Math.round(((event.currentAttendees || 0) / event.maxAttendees) * 100);
  }

  // Tags helper for form
  get tagsString(): string {
    return Array.isArray(this.formData.tags) ? this.formData.tags.join(', ') : '';
  }

  set tagsString(val: string) {
    this.formData.tags = val.split(',').map(t => t.trim()).filter(t => t);
  }
}
