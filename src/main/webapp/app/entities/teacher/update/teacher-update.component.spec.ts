import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDiscipline } from 'app/entities/discipline/discipline.model';
import { DisciplineService } from 'app/entities/discipline/service/discipline.service';
import { TeacherService } from '../service/teacher.service';
import { ITeacher } from '../teacher.model';
import { TeacherFormService } from './teacher-form.service';

import { TeacherUpdateComponent } from './teacher-update.component';

describe('Teacher Management Update Component', () => {
  let comp: TeacherUpdateComponent;
  let fixture: ComponentFixture<TeacherUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teacherFormService: TeacherFormService;
  let teacherService: TeacherService;
  let disciplineService: DisciplineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TeacherUpdateComponent],
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
      .overrideTemplate(TeacherUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeacherUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teacherFormService = TestBed.inject(TeacherFormService);
    teacherService = TestBed.inject(TeacherService);
    disciplineService = TestBed.inject(DisciplineService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Discipline query and add missing value', () => {
      const teacher: ITeacher = { id: 13207 };
      const disciplines: IDiscipline[] = [{ id: 8998 }];
      teacher.disciplines = disciplines;

      const disciplineCollection: IDiscipline[] = [{ id: 8998 }];
      jest.spyOn(disciplineService, 'query').mockReturnValue(of(new HttpResponse({ body: disciplineCollection })));
      const additionalDisciplines = [...disciplines];
      const expectedCollection: IDiscipline[] = [...additionalDisciplines, ...disciplineCollection];
      jest.spyOn(disciplineService, 'addDisciplineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(disciplineService.query).toHaveBeenCalled();
      expect(disciplineService.addDisciplineToCollectionIfMissing).toHaveBeenCalledWith(
        disciplineCollection,
        ...additionalDisciplines.map(expect.objectContaining),
      );
      expect(comp.disciplinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teacher: ITeacher = { id: 13207 };
      const discipline: IDiscipline = { id: 8998 };
      teacher.disciplines = [discipline];

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(comp.disciplinesSharedCollection).toContainEqual(discipline);
      expect(comp.teacher).toEqual(teacher);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue(teacher);
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teacherService.update).toHaveBeenCalledWith(expect.objectContaining(teacher));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue({ id: null });
      jest.spyOn(teacherService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(teacherService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teacherService.update).toHaveBeenCalled();
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
  });
});
