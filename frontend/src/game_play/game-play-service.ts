import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {gameplayUrl} from '../data.service';
import {GamePlay} from './game-play';
import {GameRoom} from '../game_room/game-room';

@Injectable({
  providedIn: 'root'
})
export class GamePlayService {
  constructor(private http: HttpClient) {
  }

  startGameplay(roomId: string): Observable<GamePlay> {
    const params = new HttpParams().set('roomId', roomId);
    return this.http.post<GamePlay>(`${gameplayUrl}`, {}, {params})
  }

  getGamePlay(gameId: string) {
    return this.http.get<GamePlay>(`${gameplayUrl}/${gameId}`)
  }

}
