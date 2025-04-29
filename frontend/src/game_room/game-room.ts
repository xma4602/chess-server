import {GameConditions} from '../game_conditions/game-conditions';
import {restRoomsLink} from '../data.service';


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

  static fromObject(obj: any): GameRoom {
    return new GameRoom(
      obj.id,
      GameConditions.fromObject(obj.gameConditions),
      obj.creatorId,
      obj.opponentId,
      obj.creatorLogin,
      obj.opponentLogin
    );
  }

  getLink(): string {
    return `${restRoomsLink}/${this.id}`
  }

}

