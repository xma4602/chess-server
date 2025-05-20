export class GameAction {
  public constructor(public codeNotation: string,
                     public algebraicNotation: string,
                     public actionType: ActionType,
                     public startPosition: string,
                     public endPosition: string,
                     public eatenPosition: string | null = null,
                     public figureCode: string | null = null) {
  }

  static fromObject(obj: {
    codeNotation: string,
    algebraicNotation: string,
    actionType: string,
    startPosition: string,
    endPosition: string,
    eatenPosition: string | null,
    figureCode: string | null
  }): GameAction {
    return new GameAction(
      obj.codeNotation,
      obj.algebraicNotation,
      ActionType.fromCode(obj.actionType)!,
      obj.startPosition,
      obj.endPosition,
      obj.eatenPosition,
      obj.figureCode
    );
  }
}

export class ActionType {
  private constructor(public readonly code: string,
                      public readonly title: string) {
  }

  static fromCode(code: string) {
    for (const x of [this.MOVE, this.EAT, this.SWAP, this.CASTLING, this.TAKING]) {
      if (x.code === code) return x
    }
    return null
  }

  static readonly MOVE = new ActionType('MOVE', 'Ход')
  static readonly EAT = new ActionType('EAT', 'Взятие')
  static readonly SWAP = new ActionType('SWAP', 'Обмен')
  static readonly CASTLING = new ActionType('CASTLING', 'Рокировка')
  static readonly TAKING = new ActionType('TAKING', 'Взятие на проходе')
}

