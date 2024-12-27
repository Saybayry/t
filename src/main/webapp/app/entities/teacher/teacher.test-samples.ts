import { ITeacher, NewTeacher } from './teacher.model';

export const sampleWithRequiredData: ITeacher = {
  id: 9510,
  fname: 'preclude unselfish legal',
  mname: 'train mentor',
};

export const sampleWithPartialData: ITeacher = {
  id: 26175,
  fname: 'broadly out freely',
  mname: 'considerate internalize oof',
};

export const sampleWithFullData: ITeacher = {
  id: 30957,
  fname: 'lest consequently',
  mname: 'crank because',
  lname: 'carefully who',
};

export const sampleWithNewData: NewTeacher = {
  fname: 'since painfully or',
  mname: 'propound make',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
