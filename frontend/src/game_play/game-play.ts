import {GameConditions} from '../game_conditions/game-conditions'
import {GameAction} from './game-action';

export enum GameState {
  BLACK_WIN = "BLACK_WIN",
  WHITE_WIN = "WHITE_WIN",
  DRAW = "DRAW",
  CONTINUES = "CONTINUES"
}

export class GamePlay {
  constructor(
    public id: string,
    public chatId: string,
    public creatorId: string,
    public opponentId: string,
    public activeUserId: string,
    public creatorLogin: string,
    public opponentLogin: string,
    public gameState: GameState,
    public gameConditions: GameConditions,
    public madeActions: string[],
    public whiteActions: GameAction[],
    public blackActions: GameAction[],
    public figures: Map<string, string>
  ) {
  }

  static fromObject(obj: any): GamePlay {
    const gameConditions = GameConditions.fromObject(obj.gameConditions);
    const whiteActions = (obj.whiteActions || []).map((action: any) => GameAction.fromObject(action));
    const blackActions = (obj.blackActions || []).map((action: any) => GameAction.fromObject(action));
    const figures = new Map<string, string>(Object.entries(obj.figures || {}));

    return new GamePlay(
      obj.id,
      obj.chatId,
      obj.creatorId,
      obj.opponentId,
      obj.activeUserId,
      obj.creatorLogin,
      obj.opponentLogin,
      obj.gameState,
      gameConditions,
      obj.madeActions ?? [],
      whiteActions,
      blackActions,
      figures
    );
  }
}

