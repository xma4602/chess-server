import {Injectable} from '@angular/core';
import {GameRoom} from './game-room';
import {GameConditions} from '../game_conditions/game-conditions';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {apiUrl} from '../data.service';

@Injectable({
  providedIn: 'root'
})
export class GameRoomService {

  private gameRoom?: GameRoom

  constructor(private http: HttpClient) {}

  getGameRoom() {
    return this.gameRoom!;
  }

  createGameRoomService(conditions: GameConditions): string {
    const userId = ''

    this.createRequest(conditions, userId).subscribe(
      (response) => {
        // Предполагаем, что response содержит данные о созданной игровой комнате
        this.gameRoom = response; // или response.id, если вам нужно только id
      },
      (error) => {
        console.error('Ошибка при создании игровой комнаты:', error);
      }
    );

    return this.gameRoom.id
  }


  createRequest(conditions: GameConditions, userId: string): Observable<GameRoom> {
    const gameRoomData = {
      gameConditions: conditions,
      firstUser: userId,
      // secondUser можно добавить позже, если нужно
    };

    return this.http.post<GameRoom>(apiUrl + '/rooms', gameRoomData);
  }

}
