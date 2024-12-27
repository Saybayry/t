import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITeacher, NewTeacher } from '../teacher.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeacher for edit and NewTeacherFormGroupInput for create.
 */
type TeacherFormGroupInput = ITeacher | PartialWithRequiredKeyOf<NewTeacher>;

type TeacherFormDefaults = Pick<NewTeacher, 'id' | 'disciplines'>;

type TeacherFormGroupContent = {
  id: FormControl<ITeacher['id'] | NewTeacher['id']>;
  fname: FormControl<ITeacher['fname']>;
  mname: FormControl<ITeacher['mname']>;
  lname: FormControl<ITeacher['lname']>;
  disciplines: FormControl<ITeacher['disciplines']>;
};

export type TeacherFormGroup = FormGroup<TeacherFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeacherFormService {
  createTeacherFormGroup(teacher: TeacherFormGroupInput = { id: null }): TeacherFormGroup {
    const teacherRawValue = {
      ...this.getFormDefaults(),
      ...teacher,
    };
    return new FormGroup<TeacherFormGroupContent>({
      id: new FormControl(
        { value: teacherRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fname: new FormControl(teacherRawValue.fname, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      mname: new FormControl(teacherRawValue.mname, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      lname: new FormControl(teacherRawValue.lname, {
        validators: [Validators.maxLength(255)],
      }),
      disciplines: new FormControl(teacherRawValue.disciplines ?? []),
    });
  }

  getTeacher(form: TeacherFormGroup): ITeacher | NewTeacher {
    return form.getRawValue() as ITeacher | NewTeacher;
  }

  resetForm(form: TeacherFormGroup, teacher: TeacherFormGroupInput): void {
    const teacherRawValue = { ...this.getFormDefaults(), ...teacher };
    form.reset(
      {
        ...teacherRawValue,
        id: { value: teacherRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeacherFormDefaults {
    return {
      id: null,
      disciplines: [],
    };
  }
}
