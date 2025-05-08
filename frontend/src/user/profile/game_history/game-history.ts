import {GameConditions} from '../../../game_conditions/game-conditions';
import {GameState} from '../../../game_play/game-play';

export class GameHistory {
  constructor(public id: string,
              public creatorLogin: string,
              public opponentLogin: string,
              public creatorRating: number,
              public opponentRating: number,
              public creatorRatingDifference: number,
              public opponentRatingDifference: number,
              public gameState: GameState,
              public gameConditions: GameConditions,
              public timestamp: string) {
  }

  static fromObject(obj: any): GameHistory {
    return new GameHistory(
      obj.id,
      obj.creatorLogin,
      obj.opponentLogin,
      obj.creatorRating,
      obj.opponentRating,
      obj.creatorRatingDifference,
      obj.opponentRatingDifference,
      GameState.fromCode(obj.gameState)!,
      GameConditions.fromObject(obj.gameConditions),
      obj.timestamp
    );
  }
}
