import {Injectable} from '@angular/core';
import {GameHistory} from './game-history';
import {HttpClient} from '@angular/common/http';
import {restHistory} from '../../../data.service';
import {map} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GameHistoryService {
  constructor(private http: HttpClient) {
  }

  getHistories(userId: string) {
    return this.http.get<Object[]>(`${restHistory}/${userId}`)
      .pipe(map(histories => histories.map(history => GameHistory.fromObject(history))))
  }
}
