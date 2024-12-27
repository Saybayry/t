import { IGroup, NewGroup } from './group.model';

export const sampleWithRequiredData: IGroup = {
  id: 8953,
  name: 'yet',
  yearGraduation: 28893,
};

export const sampleWithPartialData: IGroup = {
  id: 17193,
  name: 'mechanically disloyal gee',
  yearGraduation: 15156,
};

export const sampleWithFullData: IGroup = {
  id: 29632,
  name: 'after',
  yearGraduation: 15647,
};

export const sampleWithNewData: NewGroup = {
  name: 'however pleased',
  yearGraduation: 23230,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
