import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AssessmentResolve from './route/assessment-routing-resolve.service';

const assessmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/assessment.component').then(m => m.AssessmentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/assessment-detail.component').then(m => m.AssessmentDetailComponent),
    resolve: {
      assessment: AssessmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/assessment-update.component').then(m => m.AssessmentUpdateComponent),
    resolve: {
      assessment: AssessmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/assessment-update.component').then(m => m.AssessmentUpdateComponent),
    resolve: {
      assessment: AssessmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default assessmentRoute;
