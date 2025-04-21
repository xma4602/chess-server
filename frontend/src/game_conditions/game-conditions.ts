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

