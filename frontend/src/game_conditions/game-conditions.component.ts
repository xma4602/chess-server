import {FigureColor, GameConditions, MatchMode, TimeControl} from './game-conditions';
import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {NgForOf, NgOptimizedImage, NgClass, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {GameConditionsService} from './game-conditions-service';
import {GameRoomService} from '../game_room/game-room-service';

@Component({
  selector: 'app-game-conditions',
  standalone: true,
  templateUrl: './game-conditions.component.html',
  styleUrls: ['./game-conditions.component.css'],
  imports: [FormsModule, NgForOf, NgOptimizedImage, NgClass, NgIf],
})
export class GameConditionsComponent {
  @Input()
  gameConditions: GameConditions = new GameConditions(5, 3); // Пример значений по умолчанию
  timeControls: TimeControl[] = Object.values(TimeControl) as TimeControl[];
  matchModes: MatchMode[] = Object.values(MatchMode) as MatchMode[];
  figureColors: FigureColor[] = Object.values(FigureColor) as FigureColor[];

  constructor(private router: Router,
              private gameConditionsService: GameConditionsService,
              private gameRoomService: GameRoomService,
  ) {
  }

  ngOnInit() {
    let conditions = this.gameConditionsService.getGameConditions();
    if (conditions !== null) {
      this.gameConditions = conditions;
    }
  }

  createGame() {
    if (this.gameConditions) {
      const roomId = this.gameRoomService.createGameRoomService(this.gameConditions) // Сохраняем состояние в сервисе
      this.router.navigate([`/game-room/${roomId}`]);
    } else {
      console.error('gameConditions is null or undefined');
    }
  }

  protected readonly TimeControl = TimeControl;
}
