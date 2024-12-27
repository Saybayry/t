import { IStudent } from 'app/entities/student/student.model';
import { ILesson } from 'app/entities/lesson/lesson.model';

export interface IAssessment {
  id: number;
  isPresent?: number | null;
  assessment?: number | null;
  student?: IStudent | null;
  lesson?: ILesson | null;
}

export type NewAssessment = Omit<IAssessment, 'id'> & { id: null };
