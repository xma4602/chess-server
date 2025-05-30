import {Injectable} from '@angular/core';
import {GameRoom} from './game-room';
import {GameConditions} from '../game_conditions/game-conditions';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {restRooms} from '../data.service';
import {UserService} from '../user/user-service';
import {RequestService} from '../request.service';

@Injectable({
  providedIn: 'root'
})
export class GameRoomService {

  constructor(private requestService :RequestService,
              private userService: UserService) {
  }

  createGameRoomService(gameConditions: GameConditions): Observable<string> {
    const userId = this.userService.user?.id!
    const params = new HttpParams().set('creatorId', userId);
    const gameConditionsDto = {
      partyTime: gameConditions.partyTime,
      moveTime: gameConditions.moveTime,
      creatorFigureColor: gameConditions.creatorFigureColor.code,
      timeControl: gameConditions.timeControl.code,
      matchMode: gameConditions.matchMode.code
    }
    return this.requestService.post<string>(`${restRooms}`, gameConditionsDto, params);
  }

  connectToRoom(roomId: any): Observable<GameRoom> {
    const userId = this.userService.user?.id!
    const params = new HttpParams().set('userId', userId);

    return this.requestService.put<GameRoom>(`${restRooms}/${roomId}/join`, {}, params)
      .pipe(map(dto => GameRoom.fromObject(dto)))
  }

  closeRoom(roomId: string) {
    return this.requestService.delete<void>(`${restRooms}/${roomId}/close`)
  }
}
