import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IAssessment } from '../assessment.model';

@Component({
  selector: 'jhi-assessment-detail',
  templateUrl: './assessment-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class AssessmentDetailComponent {
  assessment = input<IAssessment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
