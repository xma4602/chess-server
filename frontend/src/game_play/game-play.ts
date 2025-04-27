import {GameConditions} from '../game_conditions/game-conditions'
import {GameAction} from './game-action';

export class GamePlay {
  constructor(
    public id: string,
    public creatorId: string,
    public opponentId: string,
    public creatorLogin: string,
    public opponentLogin: string,
    public gameConditions: GameConditions,
    public whiteActions: GameAction[],
    public blackActions: GameAction[],
    public figures: Map<string, string>
  ) {
  }
}

