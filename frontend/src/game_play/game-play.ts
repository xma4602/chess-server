import {GameConditions} from '../game_conditions/game-conditions'
import {GameAction} from './game-action';

export class GameState {
  private constructor(public readonly code: string, public readonly title: string) {}

  static fromCode(code: string) {
    for (const state of [this.BLACK_WIN, this.WHITE_WIN, this.DRAW, this.CONTINUES]) {
      if (state.code === code) return state;
    }
    return null;
  }

  static readonly BLACK_WIN = new GameState('BLACK_WIN', 'Черные выиграли');
  static readonly WHITE_WIN = new GameState('WHITE_WIN', 'Белые выиграли');
  static readonly DRAW = new GameState('DRAW', 'Ничья');
  static readonly CONTINUES = new GameState('CONTINUES', 'Игра продолжается');
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
    const gameState = GameState.fromCode(obj.gameConditions)!;
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
      gameState,
      gameConditions,
      obj.madeActions ?? [],
      whiteActions,
      blackActions,
      figures
    );
  }
}

