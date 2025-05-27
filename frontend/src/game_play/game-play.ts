import {GameConditions} from '../game_conditions/game-conditions'
import {GameAction} from './game-action';

export class GameState {
  private constructor(
    public readonly code: string,
    public readonly whiteWin: boolean,
    public readonly blackWin: boolean,
    public readonly title: string
  ) {}

  static fromCode(code: string) {
    for (const state of [
      this.BLACK_WIN_CHECKMATE,
      this.WHITE_WIN_CHECKMATE,
      this.DRAW,
      this.CONTINUES,
      this.BLACK_WIN_STALEMATE,
      this.WHITE_WIN_STALEMATE,
      this.BLACK_WIN_RESIGN,
      this.WHITE_WIN_RESIGN,
      this.WHITE_WIN_TIME_OUT,
      this.BLACK_WIN_TIME_OUT,
    ]) {
      if (state.code === code) return state;
    }
    return null;
  }

  static readonly DRAW = new GameState('DRAW', false, false, 'Ничья');
  static readonly CONTINUES = new GameState('CONTINUES', false, false, 'Игра продолжается');
  static readonly BLACK_WIN_CHECKMATE = new GameState('BLACK_WIN_CHECKMATE', false, true, 'Черные поставили мат');
  static readonly WHITE_WIN_CHECKMATE = new GameState('WHITE_WIN_CHECKMATE', true, false, 'Белые поставили мат');
  static readonly BLACK_WIN_STALEMATE = new GameState('BLACK_WIN_STALEMATE', false, false, 'Черные поставили пат');
  static readonly WHITE_WIN_STALEMATE = new GameState('WHITE_WIN_STALEMATE', false, false, 'Белые поставили пат');
  static readonly BLACK_WIN_RESIGN = new GameState('BLACK_WIN_RESIGN', false, true, 'Черные сдались');
  static readonly WHITE_WIN_RESIGN = new GameState('WHITE_WIN_RESIGN', true, false, 'Белые сдались');
  static readonly WHITE_WIN_TIME_OUT = new GameState('WHITE_WIN_TIME_OUT', true, false, 'Белые просрочили время');
  static readonly BLACK_WIN_TIME_OUT = new GameState('BLACK_WIN_TIME_OUT', false, true, 'Черные просрочили время');
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
    public creatorRating: number,
    public opponentRating: number,
    public gameState: GameState,
    public gameConditions: GameConditions,
    public madeActions: string[],
    public whiteActions: GameAction[],
    public blackActions: GameAction[],
    public figures: Map<string, string>
  ) {
  }

  static fromObject(obj:
                      {
                        id: string,
                        chatId: string,
                        creatorId: string,
                        opponentId: string,
                        activeUserId: string,
                        creatorLogin: string,
                        opponentLogin: string,
                        creatorRating: number,
                        opponentRating: number,
                        gameState: string,
                        gameConditions: any,
                        madeActions: string[],
                        whiteActions: any[],
                        blackActions: any[],
                        figures: any
                      }): GamePlay {
    const gameConditions = GameConditions.fromObject(obj.gameConditions);
    const gameState = GameState.fromCode(obj.gameState)!;
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
      obj.creatorRating,
      obj.opponentRating,
      gameState,
      gameConditions,
      obj.madeActions,
      whiteActions,
      blackActions,
      figures
    );
  }
}

