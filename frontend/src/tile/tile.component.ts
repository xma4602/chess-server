import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import {GameConditions} from '../game_conditions/game-conditions';
import {GameConditionsService} from '../game_conditions/game-conditions-service';

@Component({
  selector: 'app-tile',
  standalone: true,
  templateUrl: './tile.component.html',
  styleUrls: ['./tile.component.css']
})
export class TileComponent {
  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() gameConditions: GameConditions | null = null;

  constructor(private router: Router, private gameConditionsService: GameConditionsService) {}

  navigateToGameConditions() {
    if (this.gameConditions) {
      this.gameConditionsService.setGameConditions(this.gameConditions); // Сохраняем состояние в сервисе
      this.router.navigate(['/game-conditions']);
    } else {
      console.error('gameConditions is null or undefined');
    }
  }

}
