export class GameConditions {
  constructor(
    public partyTime: number,
    public moveTime: number,
    public id: string = '',
    public subtitle = '',
    public timeControl: TimeControl = TimeControl.WATCH,
    public matchMode: MatchMode = MatchMode.FRIENDLY,
    public figureColor: FigureColor = FigureColor.RANDOM) {
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

export enum FigureColor {
  WHITE = 'Белые',
  BLACK = 'Черные',
  RANDOM = 'Случайные'
}

