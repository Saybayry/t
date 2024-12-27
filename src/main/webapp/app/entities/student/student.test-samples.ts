import { IStudent, NewStudent } from './student.model';

export const sampleWithRequiredData: IStudent = {
  id: 15380,
  fname: 'new instead married',
  mname: 'quantify',
};

export const sampleWithPartialData: IStudent = {
  id: 28775,
  fname: 'fly gah',
  mname: 'amongst',
  lname: 'but',
};

export const sampleWithFullData: IStudent = {
  id: 6617,
  fname: 'pleasant plain petty',
  mname: 'indeed',
  lname: 'including abscond',
};

export const sampleWithNewData: NewStudent = {
  fname: 'gad even aboard',
  mname: 'drat',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
