import {Component} from '@angular/core';
import {GameRoom} from './game-room';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {GameRoomService} from './game-room-service'; // Импортируйте ваш класс GameRoom

@Component({
  selector: 'app-game-room',
  standalone: true,
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.css'],
  imports: [NgIf],
})
export class GameRoomComponent {
  gameRoom!: GameRoom; // Получаем объект GameRoom через Input

  constructor(private router: Router,
              private gameRoomService: GameRoomService) {
  }

  ngOnInit() {
    this.gameRoom = this.gameRoomService.getGameRoom();
  }


  startGame() {
    // Логика для начала игры
    console.log('Игра начата');
  }

  cancelGame() {
    // Логика для отмены игры
    console.log('Игра отменена');
  }
}
