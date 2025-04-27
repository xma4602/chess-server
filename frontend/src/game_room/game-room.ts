import {GameConditions, GameConditionsDto} from '../game_conditions/game-conditions';
import {roomsLink, restRooms} from '../data.service';


export class GameRoom {
  constructor(
    public id: string,
    public gameConditions: GameConditions,
    public creatorId: string,
    public opponentId: string,
    public creatorLogin: string,
    public opponentLogin: string,
  ) {
  }

  getLink(): string {
    return `${roomsLink}/${this.id}`
  }

}

export class GameRoomDto {
  constructor(
    public id: string,
    public gameConditions: GameConditionsDto,
    public creatorId: string,
    public opponentId: string,
    public creatorLogin: string,
    public opponentLogin: string,
  ) {
  }
}
