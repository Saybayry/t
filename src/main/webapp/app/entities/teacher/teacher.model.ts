import { IDiscipline } from 'app/entities/discipline/discipline.model';

export interface ITeacher {
  id: number;
  fname?: string | null;
  mname?: string | null;
  lname?: string | null;
  disciplines?: IDiscipline[] | null;
}

export type NewTeacher = Omit<ITeacher, 'id'> & { id: null };
