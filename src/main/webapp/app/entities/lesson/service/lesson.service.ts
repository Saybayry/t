import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILesson, NewLesson } from '../lesson.model';

export type PartialUpdateLesson = Partial<ILesson> & Pick<ILesson, 'id'>;

type RestOf<T extends ILesson | NewLesson> = Omit<T, 'classDate'> & {
  classDate?: string | null;
};

export type RestLesson = RestOf<ILesson>;

export type NewRestLesson = RestOf<NewLesson>;

export type PartialUpdateRestLesson = RestOf<PartialUpdateLesson>;

export type EntityResponseType = HttpResponse<ILesson>;
export type EntityArrayResponseType = HttpResponse<ILesson[]>;

@Injectable({ providedIn: 'root' })
export class LessonService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/lessons');

  create(lesson: NewLesson): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lesson);
    return this.http
      .post<RestLesson>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(lesson: ILesson): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lesson);
    return this.http
      .put<RestLesson>(`${this.resourceUrl}/${this.getLessonIdentifier(lesson)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(lesson: PartialUpdateLesson): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lesson);
    return this.http
      .patch<RestLesson>(`${this.resourceUrl}/${this.getLessonIdentifier(lesson)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestLesson>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLesson[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLessonIdentifier(lesson: Pick<ILesson, 'id'>): number {
    return lesson.id;
  }

  compareLesson(o1: Pick<ILesson, 'id'> | null, o2: Pick<ILesson, 'id'> | null): boolean {
    return o1 && o2 ? this.getLessonIdentifier(o1) === this.getLessonIdentifier(o2) : o1 === o2;
  }

  addLessonToCollectionIfMissing<Type extends Pick<ILesson, 'id'>>(
    lessonCollection: Type[],
    ...lessonsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const lessons: Type[] = lessonsToCheck.filter(isPresent);
    if (lessons.length > 0) {
      const lessonCollectionIdentifiers = lessonCollection.map(lessonItem => this.getLessonIdentifier(lessonItem));
      const lessonsToAdd = lessons.filter(lessonItem => {
        const lessonIdentifier = this.getLessonIdentifier(lessonItem);
        if (lessonCollectionIdentifiers.includes(lessonIdentifier)) {
          return false;
        }
        lessonCollectionIdentifiers.push(lessonIdentifier);
        return true;
      });
      return [...lessonsToAdd, ...lessonCollection];
    }
    return lessonCollection;
  }

  protected convertDateFromClient<T extends ILesson | NewLesson | PartialUpdateLesson>(lesson: T): RestOf<T> {
    return {
      ...lesson,
      classDate: lesson.classDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restLesson: RestLesson): ILesson {
    return {
      ...restLesson,
      classDate: restLesson.classDate ? dayjs(restLesson.classDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestLesson>): HttpResponse<ILesson> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestLesson[]>): HttpResponse<ILesson[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
