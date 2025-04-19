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

export enum TimeControl { WATCH, NONE }

export enum MatchMode { FRIENDLY, RATING }

export enum FigureColor { WHITE, BLACK, RANDOM }

