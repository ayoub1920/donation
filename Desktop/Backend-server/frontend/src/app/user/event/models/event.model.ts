export type EventType = 'ONLINE' | 'OUTDOOR' | 'HYBRID';

export type EventStatus = 'DRAFT' | 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';

export type TargetLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'ALL_LEVELS';

export interface Event {
  id: number;

  // Core Info
  title: string;
  description: string;
  image?: string;
  category?: string;
  tags?: string[];

  // Scheduling
  startDate: string;
  endDate?: string;
  duration?: number;
  timezone?: string;

  // Event Type (Online vs Outdoor)
  eventType: EventType;
  meetingLink?: string;
  platform?: string;
  location?: string;
  latitude?: number;
  longitude?: number;

  // Capacity & Attendance
  maxAttendees?: number;
  currentAttendees?: number;
  isRegistrationOpen?: boolean;
  registrationDeadline?: string;

  // Status & Visibility
  status: EventStatus;
  isFeatured?: boolean;
  isPublic?: boolean;

  // Host / Organizer
  hostName?: string;
  hostId?: number;
  contactEmail?: string;

  // English Learning Specific
  targetLevel?: TargetLevel;
  skillFocus?: string;

  // Metadata
  createdAt?: string;
  updatedAt?: string;
  price?: number;
  isFree?: boolean;
}
export interface ScheduleItem {
  time: string;
  title: string;
  subtitle?: string;
  eventType?: EventType;
  status?: EventStatus;
}

export interface SuggestedEvent {
  id: number;
  title: string;
  startDate: string;
  eventType: EventType;
  isFree: boolean;
  image?: string;
  category?: string;
  targetLevel?: TargetLevel;
}