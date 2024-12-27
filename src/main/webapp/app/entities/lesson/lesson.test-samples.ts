import dayjs from 'dayjs/esm';

import { ILesson, NewLesson } from './lesson.model';

export const sampleWithRequiredData: ILesson = {
  id: 5184,
  classDate: dayjs('2024-12-25'),
  classNumber: 15551,
};

export const sampleWithPartialData: ILesson = {
  id: 29987,
  classDate: dayjs('2024-12-25'),
  classNumber: 7044,
};

export const sampleWithFullData: ILesson = {
  id: 29198,
  classDate: dayjs('2024-12-25'),
  classNumber: 4064,
};

export const sampleWithNewData: NewLesson = {
  classDate: dayjs('2024-12-25'),
  classNumber: 14722,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
