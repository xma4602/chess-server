import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {restGamePlay} from '../data.service';
import {GamePlay} from './game-play';
import {UserService} from '../user/user-service';

@Injectable({
  providedIn: 'root'
})
export class GamePlayService {
  constructor(private http: HttpClient,
              private userService: UserService) {
  }

  startGameplay(roomId: string): Observable<string> {
    const params = new HttpParams().set('roomId', roomId);
    return this.http.post<string>(`${restGamePlay}`, {}, {params})
  }

  getGamePlay(gameId: string) {
    return this.http.get<GamePlay>(`${restGamePlay}/${gameId}`)
      .pipe(map(dto => GamePlay.fromObject(dto)))
  }

  makeAction(gameId: string, action: string) {
    const userId = this.userService.user?.id!;
    const params = new HttpParams()
      .set('userId', userId)
      .set('action', action);
    return this.http.post<string>(`${restGamePlay}/${gameId}/action`, {}, {params})
  }
}
