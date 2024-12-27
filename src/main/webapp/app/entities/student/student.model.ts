import { IGroup } from 'app/entities/group/group.model';

export interface IStudent {
  id: number;
  fname?: string | null;
  mname?: string | null;
  lname?: string | null;
  group?: IGroup | null;
}

export type NewStudent = Omit<IStudent, 'id'> & { id: null };
