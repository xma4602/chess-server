export class GameConditions {


  constructor(
    public partyTime: number,
    public moveTime: number,
    public creatorFigureColor: FigureColor = FigureColor.RANDOM,
    public timeControl: TimeControl = TimeControl.WATCH,
    public matchMode: MatchMode = MatchMode.FRIENDLY,
    public id: string = '',
  ) {
  }
  static fromObject(obj: any): GameConditions {
    const figureColor = FigureColor.fromCode(obj.figureColor) || FigureColor.RANDOM;
    const timeControl = TimeControl.fromCode(obj.timeControl) || TimeControl.WATCH;
    const matchMode = MatchMode.fromCode(obj.matchMode) || MatchMode.FRIENDLY;

    return new GameConditions(
      obj.partyTime,
      obj.moveTime,
      figureColor,
      timeControl,
      matchMode,
      obj.id
    );
  }

  getTileTitle(): string {
    return `${this.partyTime}+${this.moveTime}`
  }

  getTileSubtitle(): string {
    if (this.partyTime < 10) {
      return 'Блитц'
    } else if (this.partyTime < 30) {
      return 'Рапид'
    } else {
      return 'Классика'
    }
  }

}

export class TimeControl {
  private constructor(public readonly code: string,
                      public readonly title: string) {
  }

  static fromCode(code: string) {
    for (const x of [this.WATCH, this.NONE]) {
      if (x.code === code) return x
    }
    return null
  }

  static readonly WATCH = new TimeControl('WATCH', 'По времени');
  static readonly NONE = new TimeControl('NONE', 'Отсутствует');
}

export class MatchMode {
  private constructor(public readonly code: string,
                      public readonly title: string) {
  }

  static fromCode(code: string) {
    for (const x of [this.FRIENDLY, this.RATING]) {
      if (x.code === code) return x
    }
    return null
  }

  static readonly FRIENDLY = new MatchMode('FRIENDLY', 'Товарищеская')
  static readonly RATING = new MatchMode('RATING', 'Рейтинговая')
}

export class FigureColor {
  private constructor(public readonly code: string,
                      public readonly title: string) {
  }

  reverseValue(){
    return this.code === FigureColor.WHITE.code ? FigureColor.BLACK : FigureColor.WHITE;
  }
  static fromCode(code: string) {
    for (const x of [this.WHITE, this.BLACK, this.RANDOM]) {
      if (x.code === code) return x
    }
    return null
  }

  static readonly WHITE = new FigureColor('WHITE', 'Белые')
  static readonly BLACK = new FigureColor('BLACK', 'Черные')
  static readonly RANDOM = new FigureColor('RANDOM', 'Случайные')
}


