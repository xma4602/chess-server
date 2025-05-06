import { Component, Input } from '@angular/core';
import {GameHistory} from './game-history';
import {FigureColor} from '../../../game_conditions/game-conditions';


@Component({
  selector: 'app-game-history',
  standalone: true,
  templateUrl: './game-history.component.html',
  styleUrls: ['./game-history.component.css']
})
export class GameHistoryComponent {
  @Input() gameHistory!: GameHistory;

  getCreatorColor() {
    return this.gameHistory.creatorFigureColor.code === FigureColor.WHITE.code ? '♔' : '♚'
  }

  getOpponentColor() {
    return this.gameHistory.creatorFigureColor.reverseValue().code === FigureColor.WHITE.code ? '♔' : '♚'
  }
}
