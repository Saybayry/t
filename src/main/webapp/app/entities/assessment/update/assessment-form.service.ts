import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAssessment, NewAssessment } from '../assessment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAssessment for edit and NewAssessmentFormGroupInput for create.
 */
type AssessmentFormGroupInput = IAssessment | PartialWithRequiredKeyOf<NewAssessment>;

type AssessmentFormDefaults = Pick<NewAssessment, 'id'>;

type AssessmentFormGroupContent = {
  id: FormControl<IAssessment['id'] | NewAssessment['id']>;
  isPresent: FormControl<IAssessment['isPresent']>;
  assessment: FormControl<IAssessment['assessment']>;
  student: FormControl<IAssessment['student']>;
  lesson: FormControl<IAssessment['lesson']>;
};

export type AssessmentFormGroup = FormGroup<AssessmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AssessmentFormService {
  createAssessmentFormGroup(assessment: AssessmentFormGroupInput = { id: null }): AssessmentFormGroup {
    const assessmentRawValue = {
      ...this.getFormDefaults(),
      ...assessment,
    };
    return new FormGroup<AssessmentFormGroupContent>({
      id: new FormControl(
        { value: assessmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      isPresent: new FormControl(assessmentRawValue.isPresent, {
        validators: [Validators.required],
      }),
      assessment: new FormControl(assessmentRawValue.assessment, {
        validators: [Validators.required],
      }),
      student: new FormControl(assessmentRawValue.student),
      lesson: new FormControl(assessmentRawValue.lesson),
    });
  }

  getAssessment(form: AssessmentFormGroup): IAssessment | NewAssessment {
    return form.getRawValue() as IAssessment | NewAssessment;
  }

  resetForm(form: AssessmentFormGroup, assessment: AssessmentFormGroupInput): void {
    const assessmentRawValue = { ...this.getFormDefaults(), ...assessment };
    form.reset(
      {
        ...assessmentRawValue,
        id: { value: assessmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AssessmentFormDefaults {
    return {
      id: null,
    };
  }
}
