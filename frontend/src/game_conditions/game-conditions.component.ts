import {FigureColor, GameConditions, MatchMode, TimeControl} from './game-conditions';
import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {NgForOf, NgClass, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {GameConditionsService} from './game-conditions-service';
import {GameRoomService} from '../game_room/game-room-service';
import {HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-game-conditions',
  standalone: true,
  templateUrl: './game-conditions.component.html',
  styleUrls: ['./game-conditions.component.css'],
  imports: [FormsModule, NgForOf, NgClass, NgIf, HttpClientModule],
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
    this.gameConditions = this.gameConditionsService.getGameConditions()!;
  }

  createRoom() {
    if (this.gameConditions) {
      this.gameRoomService.createGameRoomService(this.gameConditions)
        .subscribe(
          (roomId) => {
            console.log('ID созданной игровой комнаты:', roomId);
            this.router.navigate([`/game-room/${roomId}`]);
          },
          (error) => {
            console.error('Ошибка при создании игровой комнаты:', error);
          }
        );
    } else {
      console.error('gameConditions is null or undefined');
    }
  }

  protected readonly TimeControl = TimeControl;
}
