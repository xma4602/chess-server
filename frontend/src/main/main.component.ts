import {Component} from '@angular/core';
import {NgForOf} from "@angular/common";
import {GameConditions} from '../game_conditions/game-conditions';
import {TileComponent} from '../tile/tile.component';
import {RouterModule} from '@angular/router'; // Импортируйте ваш класс

@Component({
  selector: 'app-main',
  standalone: true,
  templateUrl: './main.component.html',
  imports: [RouterModule, NgForOf, TileComponent],
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  gameConditionsList: GameConditions[] = [];

  constructor() {
    this.gameConditionsList.push(new GameConditions(1, 0))
    this.gameConditionsList.push(new GameConditions(2, 1))
    this.gameConditionsList.push(new GameConditions(3, 0))
    this.gameConditionsList.push(new GameConditions(3, 2))
    this.gameConditionsList.push(new GameConditions(5, 0))
    this.gameConditionsList.push(new GameConditions(5, 3))
    this.gameConditionsList.push(new GameConditions(10, 0))
    this.gameConditionsList.push(new GameConditions(10, 5))
    this.gameConditionsList.push(new GameConditions(15, 10))
    this.gameConditionsList.push(new GameConditions(30, 0))
    this.gameConditionsList.push(new GameConditions(30, 20))
  }

  getTileTitle(game: GameConditions): string {
    return `${game.partyTime}+${game.moveTime}`
  }

  getTileSubtitle(game: GameConditions): string {
    if (game.partyTime < 10) {
      return 'Блитц'
    } else if (game.partyTime < 30) {
      return 'Рапид'
    } else {
      return 'Классика'
    }
  }

}
