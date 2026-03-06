import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CourseService } from '../../../user/course/services/course.service';
import { Cours, ContenuPedagogique } from '../../../user/course/models/course.model';

@Component({
  selector: 'app-tutor-courses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './courses.component.html'
})
export class TutorCoursesComponent implements OnInit {
  courses: Cours[] = [];
  isLoading = true;
  errorMessage = '';

  // Stats
  stats = [
    { label: 'TOTAL COURSES', value: '—', sub: '', icon: 'courses' },
    { label: 'TOTAL CONTENUS', value: '—', sub: '', icon: 'students' },
    { label: 'AVG. RATING', value: '4.8', sub: 'Based on reviews', icon: 'rating' },
    { label: 'HOURS TAUGHT', value: '—', sub: '', icon: 'hours' }
  ];

  // Course CRUD
  showCourseForm = false;
  editingCourse: Cours | null = null;
  courseForm!: FormGroup;

  // Contenu CRUD
  showContenuForm = false;
  editingContenu: ContenuPedagogique | null = null;
  contenuForm!: FormGroup;
  contenuParentCoursId: number | null = null;

  // Expanded detail
  expandedCourseId: number | null = null;

  // Search
  searchTerm = '';

  constructor(
    private courseService: CourseService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadCourses();
  }

  private initForms(): void {
    this.courseForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      content: ['', Validators.required]
    });
    this.contenuForm = this.fb.group({
      titleC: ['', Validators.required],
      duration: [0, [Validators.required, Validators.min(0)]],
      contentType: ['', Validators.required]
    });
  }

  loadCourses(): void {
    this.isLoading = true;
    this.courseService.getAllCours().subscribe({
      next: (data) => {
        this.courses = data;
        this.updateStats();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load courses:', err);
        this.errorMessage = 'Failed to load courses.';
        this.isLoading = false;
      }
    });
  }

  private updateStats(): void {
    const totalContenus = this.courses.reduce((sum, c) => sum + (c.contenus?.length ?? 0), 0);
    this.stats[0].value = String(this.courses.length);
    this.stats[0].sub = 'Total courses';
    this.stats[1].value = String(totalContenus);
    this.stats[1].sub = 'Total contenus';
  }

  get filteredCourses(): Cours[] {
    if (!this.searchTerm.trim()) return this.courses;
    const term = this.searchTerm.toLowerCase();
    return this.courses.filter(c =>
      c.title.toLowerCase().includes(term) || c.description.toLowerCase().includes(term)
    );
  }

  getContentCount(course: Cours): number {
    return course.contenus?.length ?? 0;
  }

  toggleCourseDetail(courseId: number): void {
    this.expandedCourseId = this.expandedCourseId === courseId ? null : courseId;
  }

  // ── Course CRUD ──

  openCreateCourse(): void {
    this.editingCourse = null;
    this.courseForm.reset({ title: '', description: '', content: '' });
    this.showCourseForm = true;
  }

  openEditCourse(course: Cours): void {
    this.editingCourse = course;
    this.courseForm.patchValue({
      title: course.title,
      description: course.description,
      content: course.content
    });
    this.showCourseForm = true;
  }

  cancelCourseForm(): void {
    this.showCourseForm = false;
    this.editingCourse = null;
  }

  saveCourse(): void {
    if (this.courseForm.invalid) return;
    const formVal = this.courseForm.value;

    if (this.editingCourse && this.editingCourse.id) {
      this.courseService.updateCours(this.editingCourse.id, formVal).subscribe({
        next: () => { this.showCourseForm = false; this.editingCourse = null; this.loadCourses(); },
        error: (err) => console.error('Failed to update course:', err)
      });
    } else {
      this.courseService.createCours(formVal).subscribe({
        next: () => { this.showCourseForm = false; this.loadCourses(); },
        error: (err) => console.error('Failed to create course:', err)
      });
    }
  }

  deleteCourse(course: Cours): void {
    if (!course.id) return;
    if (!confirm(`Delete course "${course.title}"? This will also delete all its contenus.`)) return;
    this.courseService.deleteCours(course.id).subscribe({
      next: () => this.loadCourses(),
      error: (err) => console.error('Failed to delete course:', err)
    });
  }

  // ── Contenu CRUD ──

  openAddContenu(coursId: number): void {
    this.editingContenu = null;
    this.contenuParentCoursId = coursId;
    this.contenuForm.reset({ titleC: '', duration: 0, contentType: '' });
    this.showContenuForm = true;
  }

  openEditContenu(contenu: ContenuPedagogique, coursId: number): void {
    this.editingContenu = contenu;
    this.contenuParentCoursId = coursId;
    this.contenuForm.patchValue({
      titleC: contenu.titleC,
      duration: contenu.duration,
      contentType: contenu.contentType
    });
    this.showContenuForm = true;
  }

  cancelContenuForm(): void {
    this.showContenuForm = false;
    this.editingContenu = null;
    this.contenuParentCoursId = null;
  }

  saveContenu(): void {
    if (this.contenuForm.invalid || !this.contenuParentCoursId) return;
    const formVal = this.contenuForm.value;

    if (this.editingContenu && this.editingContenu.idContent) {
      const payload: ContenuPedagogique = {
        ...formVal,
        cours: { id: this.contenuParentCoursId }
      };
      this.courseService.updateContenu(this.editingContenu.idContent, payload).subscribe({
        next: () => { this.showContenuForm = false; this.editingContenu = null; this.loadCourses(); },
        error: (err) => console.error('Failed to update contenu:', err)
      });
    } else {
      const payload: ContenuPedagogique = {
        ...formVal,
        cours: { id: this.contenuParentCoursId }
      };
      this.courseService.createContenu(payload).subscribe({
        next: () => { this.showContenuForm = false; this.loadCourses(); },
        error: (err) => console.error('Failed to create contenu:', err)
      });
    }
  }

  deleteContenu(contenu: ContenuPedagogique): void {
    if (!contenu.idContent) return;
    if (!confirm(`Delete contenu "${contenu.titleC}"?`)) return;
    this.courseService.deleteContenu(contenu.idContent).subscribe({
      next: () => this.loadCourses(),
      error: (err) => console.error('Failed to delete contenu:', err)
    });
  }
}
