import {FigureColor} from '../game_conditions/game-conditions'

export class GamePlay {
  constructor(
    public id: string,
    public creatorId: string,
    public opponentId: string,
    public creatorLogin: string,
    public opponentLogin: string,
    public creatorIsActive: boolean,
    public creatorFigureColor: FigureColor,
    public moveTime: number,
    public startDateTime: string,
    public ended: boolean) {
  }
}

