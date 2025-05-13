import {Injectable} from '@angular/core';
import {GameHistory} from './game-history';
import {HttpClient, HttpParams} from '@angular/common/http';
import {restHistory} from '../../../data.service';
import {map} from 'rxjs';
import {RequestService} from '../../../request.service';

@Injectable({
  providedIn: 'root'
})
export class GameHistoryService {
  constructor(private requestService :RequestService) {
  }

  getHistories(userId: string) {
    return this.requestService.get<Object[]>(`${restHistory}/${userId}`)
      .pipe(map(histories => histories.map(history => GameHistory.fromObject(history))))
  }
}
