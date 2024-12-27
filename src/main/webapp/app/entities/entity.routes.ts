import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'jhipsterSampleApplicationApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'assessment',
    data: { pageTitle: 'jhipsterSampleApplicationApp.assessment.home.title' },
    loadChildren: () => import('./assessment/assessment.routes'),
  },
  {
    path: 'discipline',
    data: { pageTitle: 'jhipsterSampleApplicationApp.discipline.home.title' },
    loadChildren: () => import('./discipline/discipline.routes'),
  },
  {
    path: 'group',
    data: { pageTitle: 'jhipsterSampleApplicationApp.group.home.title' },
    loadChildren: () => import('./group/group.routes'),
  },
  {
    path: 'lesson',
    data: { pageTitle: 'jhipsterSampleApplicationApp.lesson.home.title' },
    loadChildren: () => import('./lesson/lesson.routes'),
  },
  {
    path: 'student',
    data: { pageTitle: 'jhipsterSampleApplicationApp.student.home.title' },
    loadChildren: () => import('./student/student.routes'),
  },
  {
    path: 'teacher',
    data: { pageTitle: 'jhipsterSampleApplicationApp.teacher.home.title' },
    loadChildren: () => import('./teacher/teacher.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
