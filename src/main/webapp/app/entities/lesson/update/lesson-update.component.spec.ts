import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDiscipline } from 'app/entities/discipline/discipline.model';
import { DisciplineService } from 'app/entities/discipline/service/discipline.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { ILesson } from '../lesson.model';
import { LessonService } from '../service/lesson.service';
import { LessonFormService } from './lesson-form.service';

import { LessonUpdateComponent } from './lesson-update.component';

describe('Lesson Management Update Component', () => {
  let comp: LessonUpdateComponent;
  let fixture: ComponentFixture<LessonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let lessonFormService: LessonFormService;
  let lessonService: LessonService;
  let disciplineService: DisciplineService;
  let teacherService: TeacherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LessonUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LessonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LessonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lessonFormService = TestBed.inject(LessonFormService);
    lessonService = TestBed.inject(LessonService);
    disciplineService = TestBed.inject(DisciplineService);
    teacherService = TestBed.inject(TeacherService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Discipline query and add missing value', () => {
      const lesson: ILesson = { id: 25298 };
      const discipline: IDiscipline = { id: 8998 };
      lesson.discipline = discipline;

      const disciplineCollection: IDiscipline[] = [{ id: 8998 }];
      jest.spyOn(disciplineService, 'query').mockReturnValue(of(new HttpResponse({ body: disciplineCollection })));
      const additionalDisciplines = [discipline];
      const expectedCollection: IDiscipline[] = [...additionalDisciplines, ...disciplineCollection];
      jest.spyOn(disciplineService, 'addDisciplineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lesson });
      comp.ngOnInit();

      expect(disciplineService.query).toHaveBeenCalled();
      expect(disciplineService.addDisciplineToCollectionIfMissing).toHaveBeenCalledWith(
        disciplineCollection,
        ...additionalDisciplines.map(expect.objectContaining),
      );
      expect(comp.disciplinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Teacher query and add missing value', () => {
      const lesson: ILesson = { id: 25298 };
      const teacher: ITeacher = { id: 11312 };
      lesson.teacher = teacher;

      const teacherCollection: ITeacher[] = [{ id: 11312 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [teacher];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lesson });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining),
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lesson: ILesson = { id: 25298 };
      const discipline: IDiscipline = { id: 8998 };
      lesson.discipline = discipline;
      const teacher: ITeacher = { id: 11312 };
      lesson.teacher = teacher;

      activatedRoute.data = of({ lesson });
      comp.ngOnInit();

      expect(comp.disciplinesSharedCollection).toContainEqual(discipline);
      expect(comp.teachersSharedCollection).toContainEqual(teacher);
      expect(comp.lesson).toEqual(lesson);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILesson>>();
      const lesson = { id: 5747 };
      jest.spyOn(lessonFormService, 'getLesson').mockReturnValue(lesson);
      jest.spyOn(lessonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lesson });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lesson }));
      saveSubject.complete();

      // THEN
      expect(lessonFormService.getLesson).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(lessonService.update).toHaveBeenCalledWith(expect.objectContaining(lesson));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILesson>>();
      const lesson = { id: 5747 };
      jest.spyOn(lessonFormService, 'getLesson').mockReturnValue({ id: null });
      jest.spyOn(lessonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lesson: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lesson }));
      saveSubject.complete();

      // THEN
      expect(lessonFormService.getLesson).toHaveBeenCalled();
      expect(lessonService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILesson>>();
      const lesson = { id: 5747 };
      jest.spyOn(lessonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lesson });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lessonService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDiscipline', () => {
      it('Should forward to disciplineService', () => {
        const entity = { id: 8998 };
        const entity2 = { id: 31095 };
        jest.spyOn(disciplineService, 'compareDiscipline');
        comp.compareDiscipline(entity, entity2);
        expect(disciplineService.compareDiscipline).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTeacher', () => {
      it('Should forward to teacherService', () => {
        const entity = { id: 11312 };
        const entity2 = { id: 13207 };
        jest.spyOn(teacherService, 'compareTeacher');
        comp.compareTeacher(entity, entity2);
        expect(teacherService.compareTeacher).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
