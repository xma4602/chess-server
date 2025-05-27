import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {GameConditions} from '../game_conditions/game-conditions';
import {TileComponent} from './tile/tile.component';
import {Router, RouterModule} from '@angular/router';

@Component({
  selector: 'app-main',
  standalone: true,
  templateUrl: './main.component.html',
  imports: [ RouterModule, NgForOf, TileComponent],
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  gameConditionsList: GameConditions[] = [];
  myGame: GameConditions = new GameConditions(5, 3)

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

}
