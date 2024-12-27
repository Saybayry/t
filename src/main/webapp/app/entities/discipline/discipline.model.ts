import { ITeacher } from 'app/entities/teacher/teacher.model';

export interface IDiscipline {
  id: number;
  name?: string | null;
  teachers?: ITeacher[] | null;
}

export type NewDiscipline = Omit<IDiscipline, 'id'> & { id: null };
