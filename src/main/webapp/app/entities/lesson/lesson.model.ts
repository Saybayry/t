import dayjs from 'dayjs/esm';
import { IDiscipline } from 'app/entities/discipline/discipline.model';
import { ITeacher } from 'app/entities/teacher/teacher.model';

export interface ILesson {
  id: number;
  classDate?: dayjs.Dayjs | null;
  classNumber?: number | null;
  discipline?: IDiscipline | null;
  teacher?: ITeacher | null;
}

export type NewLesson = Omit<ILesson, 'id'> & { id: null };
