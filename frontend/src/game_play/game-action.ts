export class GameAction {
  public constructor(public actionNotation: string,
                     public actionType: ActionType,
                     public startPosition: string,
                     public endPosition: string,
                     public eatenPosition: string | null = null,
                     public figureCode: string | null = null,
                     public kingStartPosition: string | null = null,
                     public rookStartPosition: string | null = null,
                     public kingEndPosition: string | null = null,
                     public rookEndPosition: string | null = null) {
  }
}

export enum ActionType {
  MOVE = 'MOVE',
  EAT = 'EAT',
  SWAP = 'SWAP',
  CASTLING = 'CASTLING',
  TAKING = 'TAKING',
}
