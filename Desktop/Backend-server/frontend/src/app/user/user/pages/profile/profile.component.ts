import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  user!: User;
  isEditing = false;
  editName = '';
  editUsername = '';
  avatarPreview: string | null = null;
  selectedFile: File | null = null;
  isSaving = false;

  stats = [
    { label: 'Courses Completed', value: '2' },
    { label: 'Quizzes Taken', value: '48' },
    { label: 'Words Learned', value: '320' },
    { label: 'Hours Practiced', value: '56' }
  ];

  achievements = [
    { title: 'First Quiz', description: 'Complete your first quiz', icon: '🎯', earned: true },
    { title: '7-Day Streak', description: 'Practice for 7 days in a row', icon: '🔥', earned: true },
    { title: 'Social Butterfly', description: 'Add 5 friends', icon: '🦋', earned: true },
    { title: 'Word Master', description: 'Learn 500 new words', icon: '📚', earned: false },
    { title: 'Grammar Guru', description: 'Score 100% on a grammar quiz', icon: '✨', earned: false },
    { title: 'Speed Reader', description: 'Complete a reading quiz in under 2 min', icon: '⚡', earned: false }
  ];

  recentActivity = [
    { action: 'Completed "Travel Essentials" Unit 3', time: '2 hours ago', icon: '📖' },
    { action: 'Scored 92% on Vocabulary Quiz', time: '5 hours ago', icon: '✅' },
    { action: 'Added Priya Patel as a friend', time: 'Yesterday', icon: '👥' },
    { action: 'Earned "7-Day Streak" badge', time: '2 days ago', icon: '🏆' }
  ];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    // 1. Load from localStorage (set during login)
    const storedUser = this.userService.getStoredUser();
    if (storedUser) {
      this.user = { ...storedUser };
    } else {
      // 2. Fallback: fetch from API if localStorage is empty
      // Replace with actual logged-in user id if you store it differently
      const fallbackId = Number(localStorage.getItem('userId'));
      if (fallbackId) {
        this.userService.getUserById(fallbackId).subscribe({
          next: (u) => {
            this.user = { ...u };
            localStorage.setItem('user', JSON.stringify(u));
          },
          error: () => console.error('Failed to load user')
        });
      }
    }
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (this.isEditing) {
      this.editName = this.user.name;
      this.editUsername = this.user.username;
      this.avatarPreview = null;
      this.selectedFile = null;
    }
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.avatarPreview = e.target?.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  saveProfile(): void {
    this.isSaving = true;

    const doUpdate = (avatarUrl?: string) => {
      // Apply edits to local user object
      this.user = {
        ...this.user,
        name: this.editName,
        username: this.editUsername,
        ...(avatarUrl ? { avatar: avatarUrl } : {})
      };

      this.userService.updateUser(this.user.id, this.user).subscribe({
        next: (updated) => {
          // Persist updated real user to localStorage
          localStorage.setItem('user', JSON.stringify(updated));
          this.user = { ...updated };
          this.isEditing = false;
          this.isSaving = false;
        },
        error: () => {
          // API unavailable — keep local changes and persist
          localStorage.setItem('user', JSON.stringify(this.user));
          this.isEditing = false;
          this.isSaving = false;
        }
      });
    };

    if (this.selectedFile) {
      this.userService.uploadAvatar(this.user.id, this.selectedFile).subscribe({
        next: (avatarUrl) => doUpdate(avatarUrl),
        error: () => {
          // Upload failed — use base64 preview locally
          if (this.avatarPreview) {
            this.user = { ...this.user, avatar: this.avatarPreview };
          }
          doUpdate();
        }
      });
    } else {
      doUpdate();
    }
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.avatarPreview = null;
    this.selectedFile = null;
  }
}