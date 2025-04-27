import {FigureColor, GameConditions} from '../game_conditions/game-conditions'
import {ActionType, GameAction} from './game-action';

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
  ) {}

  static fromObject(obj: any): GamePlay {
    const gameConditions = GameConditions.fromObject(obj.gameConditions);
    const whiteActions = (obj.whiteActions || []).map((action: any) => GameAction.fromObject(action));
    const blackActions = (obj.blackActions || []).map((action: any) => GameAction.fromObject(action));
    const figures = new Map<string, string>(Object.entries(obj.figures || {}));

    return new GamePlay(
      obj.id,
      obj.creatorId,
      obj.opponentId,
      obj.creatorLogin,
      obj.opponentLogin,
      gameConditions,
      whiteActions,
      blackActions,
      figures
    );
  }

  static readonly DEFAULT =  new GamePlay(
    'game123',
    'user1',
    'user2',
    'CreatorLogin',
    'OpponentLogin',
    new GameConditions(5, 3, FigureColor.WHITE),
    [
      new GameAction('e2-e4', ActionType.MOVE, 'e2', 'e4')
    ],
    [],
    new Map([
      ['a8', 'BLACK_ROOK'],
      ['b8', 'BLACK_KNIGHT'],
      ['c8', 'BLACK_BISHOP'],
      ['d8', 'BLACK_QUEEN'],
      ['e8', 'BLACK_KING'],
      ['f8', 'BLACK_BISHOP'],
      ['g8', 'BLACK_KNIGHT'],
      ['h8', 'BLACK_ROOK'],
      ['a7', 'BLACK_PAWN'],
      ['b7', 'BLACK_PAWN'],
      ['c7', 'BLACK_PAWN'],
      ['d7', 'BLACK_PAWN'],
      ['e7', 'BLACK_PAWN'],
      ['f7', 'BLACK_PAWN'],
      ['g7', 'BLACK_PAWN'],
      ['h7', 'BLACK_PAWN'],
      ['a1', 'WHITE_ROOK'],
      ['b1', 'WHITE_KNIGHT'],
      ['c1', 'WHITE_BISHOP'],
      ['d1', 'WHITE_QUEEN'],
      ['e1', 'WHITE_KING'],
      ['f1', 'WHITE_BISHOP'],
      ['g1', 'WHITE_KNIGHT'],
      ['h1', 'WHITE_ROOK'],
      ['a2', 'WHITE_PAWN'],
      ['b2', 'WHITE_PAWN'],
      ['c2', 'WHITE_PAWN'],
      ['d2', 'WHITE_PAWN'],
      ['e2', 'WHITE_PAWN'],
      ['f2', 'WHITE_PAWN'],
      ['g2', 'WHITE_PAWN'],
      ['h2', 'WHITE_PAWN'],
    ])
  );
}

