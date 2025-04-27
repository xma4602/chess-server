import {Injectable} from '@angular/core';
import {GameRoom, GameRoomDto} from './game-room';
import {
  FigureColor,
  GameConditions,
  GameConditionsDto,
  MatchMode,
  TimeControl
} from '../game_conditions/game-conditions';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {restRooms} from '../data.service';
import {UserService} from '../user/user-service';

@Injectable({
  providedIn: 'root'
})
export class GameRoomService {

  constructor(private http: HttpClient,
              private userService: UserService) {
  }

  createGameRoomService(gameConditions: GameConditions): Observable<string> {
    const userId = this.userService.user?.id!
    const params = new HttpParams().set('creatorId', userId);
    const gameConditionsDto = new GameConditionsDto(
      gameConditions.partyTime,
      gameConditions.moveTime,
      gameConditions.figureColor.code,
      gameConditions.timeControl.code,
      gameConditions.matchMode.code
    )
    return this.http.post<string>(`${restRooms}`, gameConditionsDto, {params});
  }

  connectToRoom(roomId: any): Observable<GameRoom> {
    const userId = this.userService.user?.id!
    const params = new HttpParams().set('userId', userId);

    return this.http.put<GameRoomDto>(`${restRooms}/${roomId}/join`, {}, {params})
      .pipe(map(dto => this.parseRoom(dto)))
  }

  parseRoom(dto: GameRoomDto) {
    const gameConditions = new GameConditions(
      dto.gameConditions.partyTime,
      dto.gameConditions.moveTime,
      FigureColor.fromCode(dto.gameConditions.figureColor)!,
      TimeControl.fromCode(dto.gameConditions.timeControl)!,
      MatchMode.fromCode(dto.gameConditions.matchMode)!
    )
    return new GameRoom(
      dto.id, gameConditions,
      dto.creatorId, dto.opponentId,
      dto.creatorLogin, dto.opponentLogin
    )
  }
}
