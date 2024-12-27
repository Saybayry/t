import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { DisciplineService } from '../service/discipline.service';
import { IDiscipline } from '../discipline.model';
import { DisciplineFormService } from './discipline-form.service';

import { DisciplineUpdateComponent } from './discipline-update.component';

describe('Discipline Management Update Component', () => {
  let comp: DisciplineUpdateComponent;
  let fixture: ComponentFixture<DisciplineUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let disciplineFormService: DisciplineFormService;
  let disciplineService: DisciplineService;
  let teacherService: TeacherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DisciplineUpdateComponent],
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
      .overrideTemplate(DisciplineUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DisciplineUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    disciplineFormService = TestBed.inject(DisciplineFormService);
    disciplineService = TestBed.inject(DisciplineService);
    teacherService = TestBed.inject(TeacherService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Teacher query and add missing value', () => {
      const discipline: IDiscipline = { id: 31095 };
      const teachers: ITeacher[] = [{ id: 11312 }];
      discipline.teachers = teachers;

      const teacherCollection: ITeacher[] = [{ id: 11312 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [...teachers];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ discipline });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining),
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const discipline: IDiscipline = { id: 31095 };
      const teacher: ITeacher = { id: 11312 };
      discipline.teachers = [teacher];

      activatedRoute.data = of({ discipline });
      comp.ngOnInit();

      expect(comp.teachersSharedCollection).toContainEqual(teacher);
      expect(comp.discipline).toEqual(discipline);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscipline>>();
      const discipline = { id: 8998 };
      jest.spyOn(disciplineFormService, 'getDiscipline').mockReturnValue(discipline);
      jest.spyOn(disciplineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discipline });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discipline }));
      saveSubject.complete();

      // THEN
      expect(disciplineFormService.getDiscipline).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(disciplineService.update).toHaveBeenCalledWith(expect.objectContaining(discipline));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscipline>>();
      const discipline = { id: 8998 };
      jest.spyOn(disciplineFormService, 'getDiscipline').mockReturnValue({ id: null });
      jest.spyOn(disciplineService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discipline: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discipline }));
      saveSubject.complete();

      // THEN
      expect(disciplineFormService.getDiscipline).toHaveBeenCalled();
      expect(disciplineService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiscipline>>();
      const discipline = { id: 8998 };
      jest.spyOn(disciplineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discipline });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(disciplineService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
