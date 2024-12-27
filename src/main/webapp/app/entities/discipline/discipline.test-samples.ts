import { IDiscipline, NewDiscipline } from './discipline.model';

export const sampleWithRequiredData: IDiscipline = {
  id: 23439,
  name: 'mealy',
};

export const sampleWithPartialData: IDiscipline = {
  id: 2315,
  name: 'worth',
};

export const sampleWithFullData: IDiscipline = {
  id: 19917,
  name: 'round able',
};

export const sampleWithNewData: NewDiscipline = {
  name: 'innocently dwell like',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
