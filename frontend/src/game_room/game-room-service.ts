import {Injectable} from '@angular/core';
import {GameRoom} from './game-room';
import {GameConditions} from '../game_conditions/game-conditions';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {restUrl, roomsUrl} from '../data.service';

@Injectable({
  providedIn: 'root'
})
export class GameRoomService {

  constructor(private http: HttpClient) {
  }

  createGameRoomService(conditions: GameConditions): Observable<string> {
    const userId = ''
    const gameRoomData = {
      gameConditions: conditions,
      firstUser: userId,
    };
    const params = new HttpParams().set('creatorId', userId);
    return this.http.post<string>(`${restUrl}/${roomsUrl}`, gameRoomData, {params});
  }

  connectToRoom(roomId: any): Observable<GameRoom> {
    const userId = ''
    const params = new HttpParams().set('userId', userId);

    return this.http.put<GameRoom>(`${restUrl}/${roomsUrl}/${roomId}/join`, {}, {params})
  }
}
