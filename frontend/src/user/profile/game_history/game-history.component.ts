import {Component, Input} from '@angular/core';
import {GameHistory} from './game-history';
import {FigureColor, TimeControl} from '../../../game_conditions/game-conditions';
import {GameState} from '../../../game_play/game-play';
import {UserService} from '../../user-service';


@Component({
  selector: 'app-game-history',
  standalone: true,
  templateUrl: './game-history.component.html',
  styleUrls: ['./game-history.component.css']
})
export class GameHistoryComponent {
  @Input() gameHistory!: GameHistory;

  constructor(private userService: UserService) {
  }

  getLogin(figureColor: FigureColor) {
    return this.gameHistory.gameConditions.creatorFigureColor.code == figureColor.code ?
      `${this.gameHistory.creatorLogin}` :
      `${this.gameHistory.opponentLogin}`;
  }

  getRating(figureColor: FigureColor) {
    if (this.gameHistory.gameConditions.creatorFigureColor.code == figureColor.code) {
      const rating = this.gameHistory.creatorRating.toString()
      let ratingDif = `${this.gameHistory.creatorRatingDifference >= 0 ? '+' : ''}${this.gameHistory.creatorRatingDifference}`
      return `${rating} (${ratingDif})`
    } else {
      const rating = this.gameHistory.opponentRating.toString()
      let ratingDif = `${this.gameHistory.opponentRatingDifference >= 0 ? '+' : ''}${this.gameHistory.opponentRatingDifference}`
      return `${rating} (${ratingDif})`
    }
  }

  getGameState() {
    const userLogin = this.userService.user!.login!;
    const userFigureColor = this.gameHistory.creatorLogin === userLogin ?
      this.gameHistory.gameConditions.creatorFigureColor :
      this.gameHistory.gameConditions.creatorFigureColor.reverseValue();

    switch (this.gameHistory.gameState) {
      case GameState.DRAW:
        return 'Ничья'
      case GameState.WHITE_WIN_CHECKMATE:
        return userFigureColor.code === FigureColor.WHITE.code ?
          'Мат черным' :
          'Мат белым'
      case GameState.BLACK_WIN_CHECKMATE:
        return userFigureColor.code === FigureColor.BLACK.code ?
          'Мат белым' :
          'Мат черным'
      case GameState.WHITE_WIN_RESIGN:
        return 'Черные сдались'
      case GameState.BLACK_WIN_RESIGN:
        return 'Белые сдались'
      case GameState.WHITE_WIN_TIME_OUT:
        return 'Черные проиграли по времени'
      case GameState.BLACK_WIN_TIME_OUT:
        return 'Белые проиграли по времени'
      case GameState.WHITE_WIN_STALEMATE:
        return 'Пат черным'
      case GameState.BLACK_WIN_STALEMATE:
        return 'Пат белым'
      default:
        return 'Игра продолжается'
    }
  }


  protected readonly FigureColor = FigureColor;

  getTimeControl() {
    if (this.gameHistory.gameConditions.timeControl.code == TimeControl.WATCH.code) {
      return this.gameHistory.gameConditions.getTileSubtitle() + ' ' + this.gameHistory.gameConditions.getTileTitle()
    } else {
      return 'Без времени'
    }
  }
}
