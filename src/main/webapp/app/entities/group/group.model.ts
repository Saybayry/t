export interface IGroup {
  id: number;
  name?: string | null;
  yearGraduation?: number | null;
}

export type NewGroup = Omit<IGroup, 'id'> & { id: null };
