import { Injectable } from '@angular/core';
import { GameConditions } from './game-conditions';

@Injectable({
  providedIn: 'root'
})
export class GameConditionsService {
  private gameConditions: GameConditions | null = null;

  setGameConditions(conditions: GameConditions) {
    this.gameConditions = conditions;
  }

  getGameConditions(): GameConditions | null {
    return this.gameConditions;
  }
}
