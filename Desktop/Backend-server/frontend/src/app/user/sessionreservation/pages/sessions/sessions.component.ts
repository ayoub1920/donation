import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { Session, Certification, PracticeItem } from '../../models/sessionReservation.model';

@Component({
  selector: 'app-sessions',
  standalone: true,
  templateUrl: './sessions.component.html'
})
export class SessionsComponent implements OnInit {
  sessions: Session[] = [];
  certifications: Certification[] = [];
  practiceItems: PracticeItem[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private sessionService: SessionService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadSessions();
    this.loadCertifications();
    this.loadPracticeItems();
  }

  private loadSessions(): void {
    this.sessionService.getAllSessions().subscribe({
      next: (data) => {
        this.sessions = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load sessions:', err);
        this.errorMessage = 'Failed to load sessions. Please try again later.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private loadCertifications(): void {
    this.sessionService.getAllCertifications().subscribe({
      next: (data) => {
        this.certifications = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load certifications:', err);
      }
    });
  }

  private loadPracticeItems(): void {
    this.sessionService.getAllPracticeItems().subscribe({
      next: (data) => {
        this.practiceItems = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load practice items:', err);
      }
    });
  }
}