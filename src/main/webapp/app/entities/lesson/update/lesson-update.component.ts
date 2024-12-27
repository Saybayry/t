import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDiscipline } from 'app/entities/discipline/discipline.model';
import { DisciplineService } from 'app/entities/discipline/service/discipline.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { LessonService } from '../service/lesson.service';
import { ILesson } from '../lesson.model';
import { LessonFormGroup, LessonFormService } from './lesson-form.service';

@Component({
  selector: 'jhi-lesson-update',
  templateUrl: './lesson-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LessonUpdateComponent implements OnInit {
  isSaving = false;
  lesson: ILesson | null = null;

  disciplinesSharedCollection: IDiscipline[] = [];
  teachersSharedCollection: ITeacher[] = [];

  protected lessonService = inject(LessonService);
  protected lessonFormService = inject(LessonFormService);
  protected disciplineService = inject(DisciplineService);
  protected teacherService = inject(TeacherService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LessonFormGroup = this.lessonFormService.createLessonFormGroup();

  compareDiscipline = (o1: IDiscipline | null, o2: IDiscipline | null): boolean => this.disciplineService.compareDiscipline(o1, o2);

  compareTeacher = (o1: ITeacher | null, o2: ITeacher | null): boolean => this.teacherService.compareTeacher(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lesson }) => {
      this.lesson = lesson;
      if (lesson) {
        this.updateForm(lesson);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lesson = this.lessonFormService.getLesson(this.editForm);
    if (lesson.id !== null) {
      this.subscribeToSaveResponse(this.lessonService.update(lesson));
    } else {
      this.subscribeToSaveResponse(this.lessonService.create(lesson));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILesson>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(lesson: ILesson): void {
    this.lesson = lesson;
    this.lessonFormService.resetForm(this.editForm, lesson);

    this.disciplinesSharedCollection = this.disciplineService.addDisciplineToCollectionIfMissing<IDiscipline>(
      this.disciplinesSharedCollection,
      lesson.discipline,
    );
    this.teachersSharedCollection = this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(
      this.teachersSharedCollection,
      lesson.teacher,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.disciplineService
      .query()
      .pipe(map((res: HttpResponse<IDiscipline[]>) => res.body ?? []))
      .pipe(
        map((disciplines: IDiscipline[]) =>
          this.disciplineService.addDisciplineToCollectionIfMissing<IDiscipline>(disciplines, this.lesson?.discipline),
        ),
      )
      .subscribe((disciplines: IDiscipline[]) => (this.disciplinesSharedCollection = disciplines));

    this.teacherService
      .query()
      .pipe(map((res: HttpResponse<ITeacher[]>) => res.body ?? []))
      .pipe(map((teachers: ITeacher[]) => this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(teachers, this.lesson?.teacher)))
      .subscribe((teachers: ITeacher[]) => (this.teachersSharedCollection = teachers));
  }
}
