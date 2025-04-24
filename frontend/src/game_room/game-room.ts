import {GameConditions} from '../game_conditions/game-conditions';
import {roomsUrl} from '../data.service';


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
    return roomsUrl + this.id
  }

}
