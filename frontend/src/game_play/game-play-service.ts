import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs';
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

  startGameplay(roomId: string) {
    const params = new HttpParams().set('roomId', roomId);
    return this.http.post<void>(`${restGamePlay}`, {}, {params})
  }

  getGamePlay(gameId: string) {
    return this.http.get<any>(`${restGamePlay}/${gameId}`)
      .pipe(map(dto => GamePlay.fromObject(dto)))
  }

  makeAction(gameId: string, action: string) {
    const userId = this.userService.user?.id!;
    const params = new HttpParams()
      .set('userId', userId)
      .set('action', action);
    return this.http.post<void>(`${restGamePlay}/${gameId}/action`, {}, {params})
  }

  timeout(gameId: string, userId: string) {
    const params = new HttpParams()
      .set('userId', userId);
    return this.http.put<void>(`${restGamePlay}/${gameId}/timeout`, {}, {params})
  }

  requestDraw(gameId: string, userId: string) {
    const params = new HttpParams()
      .set('userId', userId);
    return this.http.put<void>(`${restGamePlay}/${gameId}/draw/request`, {}, {params})
  }

  responseDraw(gameId: string, userId: string, result: boolean) {
    const params = new HttpParams()
      .set('userId', userId);
    return this.http.put<void>(`${restGamePlay}/${gameId}/draw/response`, result, {params})
  }
  surrender(gameId: string, userId: string) {
    const params = new HttpParams()
      .set('userId', userId);
    return this.http.put<void>(`${restGamePlay}/${gameId}/surrender`, {}, {params})
  }

}
