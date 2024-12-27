import { IAssessment, NewAssessment } from './assessment.model';

export const sampleWithRequiredData: IAssessment = {
  id: 4003,
  isPresent: 7747,
  assessment: 9425,
};

export const sampleWithPartialData: IAssessment = {
  id: 11980,
  isPresent: 29688,
  assessment: 23967,
};

export const sampleWithFullData: IAssessment = {
  id: 1893,
  isPresent: 5084,
  assessment: 356,
};

export const sampleWithNewData: NewAssessment = {
  isPresent: 28127,
  assessment: 13019,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
