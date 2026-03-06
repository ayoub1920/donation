import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuizService } from '../services/quiz.service';
import { QuizCard, QuizCategory, Quiz, QuestionQuiz, QuizLevel, QuizStatus, QuestionType } from '../models/quiz.model';
import { AuthService } from '../../../shared/services/auth.service';
import { MOCK_USER } from '../../../shared/constants/mock-data';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [CommonModule, DecimalPipe, ReactiveFormsModule],
  templateUrl: './quiz.component.html'
})
export class QuizComponent implements OnInit {
  quizCards: QuizCard[] = [];
  categories: QuizCategory[] = [];
  quizzes: Quiz[] = [];
  user = MOCK_USER;
  activeTab = 'Vocabulary';
  tabs = ['Vocabulary', 'Reading', 'Grammar', 'Listening'];
  focusTags = ['New words', 'Reading speed', 'Grammar review', 'Listening'];
  isLoading = true;
  errorMessage = '';

  showQuizForm = false;
  editingQuiz: Quiz | null = null;
  quizForm!: FormGroup;

  showQuestionForm = false;
  editingQuestion: QuestionQuiz | null = null;
  questionForm!: FormGroup;
  questionParentQuizId: number | null = null;

  expandedQuizId: number | null = null;

  quizLevels = Object.values(QuizLevel);
  quizStatuses = Object.values(QuizStatus);
  questionTypes = Object.values(QuestionType);

  constructor(
    private quizService: QuizService,
    private authService: AuthService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef  // <-- injected
  ) {}

  get isTutor(): boolean {
    return this.authService.userRole === 'TUTEUR';
  }

  ngOnInit(): void {
    this.initForms();
    this.loadQuizCards();
    this.loadQuizCategories();
    this.loadQuizzes();
  }

  private initForms(): void {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      level: [QuizLevel.BEGINNER, Validators.required],
      dateStart: ['', Validators.required],
      dateEnd: ['', Validators.required],
      status: [QuizStatus.DRAFT, Validators.required],
      courseId: [null],
      xpReward: [0, [Validators.required, Validators.min(0)]]
    });

    this.questionForm = this.fb.group({
      question: ['', Validators.required],
      options: this.fb.array([
        this.fb.control('', Validators.required),
        this.fb.control('', Validators.required)
      ]),
      correctAnswer: ['', Validators.required],
      explanation: ['', Validators.required],
      type: [QuestionType.MCQ, Validators.required]
    });
  }

  get optionsArray(): FormArray {
    return this.questionForm.get('options') as FormArray;
  }

  addOption(): void {
    this.optionsArray.push(this.fb.control('', Validators.required));
  }

  removeOption(index: number): void {
    if (this.optionsArray.length > 2) {
      this.optionsArray.removeAt(index);
    }
  }

  loadQuizCards(): void {
    this.quizService.getAllQuizCards().subscribe({
      next: (data) => {
        this.quizCards = data;
        this.isLoading = false;
        this.cdr.detectChanges();  // <--
      },
      error: (err) => {
        console.error('Failed to load quiz cards:', err);
        this.errorMessage = 'Failed to load quizzes. Please try again later.';
        this.isLoading = false;
        this.cdr.detectChanges();  // <--
      }
    });
  }

  loadQuizCategories(): void {
    this.quizService.getAllQuizCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();  // <--
      },
      error: (err) => console.error('Failed to load quiz categories:', err)
    });
  }

  loadQuizzes(): void {
    this.quizService.getAllQuizzes().subscribe({
      next: (data) => {
        this.quizzes = data;
        this.cdr.detectChanges();  // <--
      },
      error: (err) => console.error('Failed to load quizzes:', err)
    });
  }

  toggleQuizDetail(quizId: number): void {
    this.expandedQuizId = this.expandedQuizId === quizId ? null : quizId;
  }

  // ── Quiz CRUD ──

  openCreateQuiz(): void {
    this.editingQuiz = null;
    this.quizForm.reset({
      title: '', description: '', level: QuizLevel.BEGINNER,
      dateStart: '', dateEnd: '', status: QuizStatus.DRAFT,
      courseId: null, xpReward: 0
    });
    this.showQuizForm = true;
  }

  openEditQuiz(quiz: Quiz): void {
    this.editingQuiz = quiz;
    this.quizForm.patchValue({
      title: quiz.title, description: quiz.description, level: quiz.level,
      dateStart: quiz.dateStart, dateEnd: quiz.dateEnd, status: quiz.status,
      courseId: quiz.courseId, xpReward: quiz.xpReward
    });
    this.showQuizForm = true;
  }

  cancelQuizForm(): void {
    this.showQuizForm = false;
    this.editingQuiz = null;
  }

  saveQuiz(): void {
    if (this.quizForm.invalid) return;
    const formVal = this.quizForm.value;

    if (this.editingQuiz && this.editingQuiz.id) {
      this.quizService.updateQuiz(this.editingQuiz.id, formVal).subscribe({
        next: () => { this.showQuizForm = false; this.editingQuiz = null; this.loadQuizzes(); },
        error: (err) => console.error('Failed to update quiz:', err)
      });
    } else {
      this.quizService.createQuiz(formVal).subscribe({
        next: () => { this.showQuizForm = false; this.loadQuizzes(); },
        error: (err) => console.error('Failed to create quiz:', err)
      });
    }
  }

  deleteQuiz(quiz: Quiz): void {
    if (!quiz.id) return;
    if (!confirm(`Delete quiz "${quiz.title}"? This will also delete all its questions.`)) return;
    this.quizService.deleteQuiz(quiz.id).subscribe({
      next: () => this.loadQuizzes(),
      error: (err) => console.error('Failed to delete quiz:', err)
    });
  }

  // ── Question CRUD ──

  openAddQuestion(quizId: number): void {
    this.editingQuestion = null;
    this.questionParentQuizId = quizId;
    this.questionForm.reset({ question: '', correctAnswer: '', explanation: '', type: QuestionType.MCQ });
    while (this.optionsArray.length > 2) this.optionsArray.removeAt(this.optionsArray.length - 1);
    while (this.optionsArray.length < 2) this.optionsArray.push(this.fb.control('', Validators.required));
    this.optionsArray.controls.forEach(c => c.setValue(''));
    this.showQuestionForm = true;
  }

  openEditQuestion(q: QuestionQuiz, quizId: number): void {
    this.editingQuestion = q;
    this.questionParentQuizId = quizId;
    this.questionForm.patchValue({
      question: q.question, correctAnswer: q.correctAnswer,
      explanation: q.explanation, type: q.type
    });
    while (this.optionsArray.length > 0) this.optionsArray.removeAt(0);
    q.options.forEach(opt => this.optionsArray.push(this.fb.control(opt, Validators.required)));
    this.showQuestionForm = true;
  }

  cancelQuestionForm(): void {
    this.showQuestionForm = false;
    this.editingQuestion = null;
    this.questionParentQuizId = null;
  }

  saveQuestion(): void {
    if (this.questionForm.invalid || !this.questionParentQuizId) return;
    const formVal = this.questionForm.value;
    const payload: QuestionQuiz = { ...formVal, quiz: { id: this.questionParentQuizId } };

    if (this.editingQuestion && this.editingQuestion.id) {
      this.quizService.updateQuestion(this.editingQuestion.id, payload).subscribe({
        next: () => { this.showQuestionForm = false; this.editingQuestion = null; this.loadQuizzes(); },
        error: (err) => console.error('Failed to update question:', err)
      });
    } else {
      this.quizService.createQuestion(payload).subscribe({
        next: () => { this.showQuestionForm = false; this.loadQuizzes(); },
        error: (err) => console.error('Failed to create question:', err)
      });
    }
  }

  deleteQuestion(q: QuestionQuiz): void {
    if (!q.id) return;
    if (!confirm('Delete this question?')) return;
    this.quizService.deleteQuestion(q.id).subscribe({
      next: () => this.loadQuizzes(),
      error: (err) => console.error('Failed to delete question:', err)
    });
  }
}