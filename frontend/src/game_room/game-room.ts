import {GameConditions} from '../game_conditions/game-conditions';
import {User} from '../user/user';
import {roomsBaseLink} from '../data.service';


export class GameRoom {
  constructor(
    public id: string,
    public gameConditions: GameConditions,
    public firstUser: User,
    public secondUser?: User,
  ) {
  }

  getLink(): string {
    return roomsBaseLink + this.id
  }

}
