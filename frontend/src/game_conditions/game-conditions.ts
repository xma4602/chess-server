export class GameConditions {
  constructor(
    public partyTime: number,
    public moveTime: number,
    public figureColor: FigureColor = FigureColor.RANDOM,
    public id: string = '',
    public subtitle = '',
    public timeControl: TimeControl = TimeControl.WATCH,
    public matchMode: MatchMode = MatchMode.FRIENDLY,
  ) {
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

export enum TimeControl {
  WATCH = 'По времени',
  NONE = 'Отсутствует'
}

export enum MatchMode {
  FRIENDLY = 'Товарищеская',
  RATING = 'Рейтинговая'
}

export class FigureColor {
  private constructor(private code: string,
                      private title: string) {
  }

  static readonly WHITE = new FigureColor('WHITE', 'Белые')
  static readonly BLACK = new FigureColor('BLACK', 'Черные')
  static readonly RANDOM = new FigureColor('RANDOM', 'Случайные')
}

