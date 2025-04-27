import {Component, OnInit} from '@angular/core';
import {GameRoom} from './game-room';
import {NgIf} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {GameRoomService} from './game-room-service';
import {HttpClientModule} from '@angular/common/http';
import {GamePlayService} from '../game_play/game-play-service';
import {StompService} from '../stomp.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {wsRooms} from '../data.service';

@Component({
  selector: 'app-game-room',
  standalone: true,
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.css'],
  imports: [NgIf, HttpClientModule],
})
export class GameRoomComponent implements OnInit {
  gameRoom!: GameRoom; // Получаем объект GameRoom через Input

  constructor(private router: Router,
              private route: ActivatedRoute,
              private snackBar: MatSnackBar,
              private gameRoomService: GameRoomService,
              private gamePlayService: GamePlayService,
              private stompService: StompService
  ) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const roomId = params.get('id')!; // Получаем roomId из параметров маршрута
      console.log('Полученный ID комнаты:', roomId); // Логируем roomId

      this.gameRoomService.connectToRoom(roomId).subscribe(
        (room) => {
          console.log('Успешно подключились к комнате:', room); // Логируем информацию о комнате
          this.gameRoom = room; // Сохраняем комнату в переменной

          this.stompService.subscribe(`${wsRooms}/${roomId}/join`, message => {
            const obj = JSON.parse(message.body);
            this.gameRoom = GameRoom.fromObject(obj);
          }).then(() => {
              this.stompService.subscribe(`${wsRooms}/${roomId}/startGame`, message => {
                const gameId = message.body
                console.log('ID созданной игровой комнаты:', gameId);
                this.router.navigate([`/game-play/${gameId}`]);
              })
            }
          )


        },
        (error) => {
          console.error('Ошибка при подключении к комнате:', error); // Логируем ошибку
        }
      );
    });
  }


  startGame() {
    if (!!this.gameRoom && this.gameRoom?.opponentLogin) {
      this.gamePlayService.startGameplay(this.gameRoom.id).subscribe(
        response => {
          console.log('Игра создана:', response);
        },
        (error) => {
          console.error('Ошибка при создании игры:', error);
          this.openSnackBar('Ошибка при создании игры: ' + error.error.message, 'Close');
        }
      );
    } else {
      console.error('gameRoom is null or undefined');
    }
  }

  cancelGame() {
    // Логика для отмены игры
    console.log('Игра отменена');
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }
}
