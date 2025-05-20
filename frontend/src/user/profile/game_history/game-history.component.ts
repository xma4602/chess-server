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
    const userFigureColor = this.gameHistory.creatorLogin == userLogin ?
      this.gameHistory.gameConditions.creatorFigureColor : this.gameHistory.gameConditions.creatorFigureColor.reverseValue()
    const gameState = this.gameHistory.gameState;

    if (gameState === GameState.DRAW) {
      return 'Ничья'
    } else if (
      (userFigureColor.code === FigureColor.WHITE.code && gameState === GameState.WHITE_WIN) ||
      (userFigureColor.code === FigureColor.BLACK.code && gameState === GameState.BLACK_WIN)
    ) {
      return 'Вы победили'
    } else {
      return 'Вы проиграли'
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
