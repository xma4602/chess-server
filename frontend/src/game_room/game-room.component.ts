import {Component, OnInit} from '@angular/core';
import {GameRoom} from './game-room';
import {CommonModule, NgIf} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {GameRoomService} from './game-room-service';
import {HttpClientModule} from '@angular/common/http';
import {GamePlayService} from '../game_play/game-play-service';
import {StompService} from '../stomp.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {wsRooms} from '../data.service';
import {UserService} from '../user/user-service';
import {IMessage} from '@stomp/stompjs';
import {ConfirmDialogComponent} from '../game_play/dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {AuthModule} from '../app/auth.module';

@Component({
  selector: 'app-game-room',
  standalone: true,
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.css'],
  imports: [AuthModule,NgIf, HttpClientModule, CommonModule],
})
export class GameRoomComponent implements OnInit {
  gameRoom!: GameRoom;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private snackBar: MatSnackBar,
              public userService: UserService,
              private gameRoomService: GameRoomService,
              private gamePlayService: GamePlayService,
              private stompService: StompService,
              private dialog: MatDialog,
  ) {
  }

  ngOnInit() {
    // Подписываемся на изменения параметров маршрута
    this.route.paramMap.subscribe(params => {
      const roomId = params.get('id')!; // Получаем roomId из параметров маршрута
      console.log('Полученный ID комнаты:', roomId); // Логируем полученный ID комнаты

      // Подключаемся к комнате по ID
      this.gameRoomService.connectToRoom(roomId).subscribe(
        (room) => {
          console.log('Успешно подключились к комнате:', room); // Логируем информацию о комнате
          this.gameRoom = room; // Сохраняем комнату в переменной

          // Подписываемся на события STOMP для этой комнаты
          this.stompService.subscribe(`${wsRooms}/${roomId}/join`, (message) => this.onJoin(message))
            .then(() => this.stompService.subscribe(`${wsRooms}/${roomId}/startGame`, (message) => this.onStartGame(message))
              .then(() => this.stompService.subscribe(`${wsRooms}/${roomId}/close`, (message) => this.onClose(message)))
            );
        },
        (error) => {
          console.error('Ошибка при подключении к комнате:', error); // Логируем ошибку подключения
        }
      );
    }, console.error);
  }

  // Обработка события закрытия комнаты
  private onClose(message: IMessage) {
    console.log('Получено сообщение о закрытии комнаты:', message); // Логируем сообщение о закрытии
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Комната была закрыта',
        message: 'Вы исключены из комнаты'
      }
    }).afterClosed().subscribe(
      () => {
        console.log('Игровая комната закрыта'); // Логируем закрытие комнаты
        this.router.navigate(['']); // Перенаправляем на главную страницу
      },
      (error) => {
        console.error('Ошибка при закрытии диалога:', error); // Логируем ошибку при закрытии диалога
      }
    );
  }

  // Обработка события начала игры
  private onStartGame(message: IMessage) {
    const gameId = message.body; // Получаем ID созданной игры из сообщения
    console.log('ID созданной игровой комнаты:', gameId); // Логируем ID игры
    this.router.navigate([`/game-play/${gameId}`]); // Перенаправляем на страницу игры
  }

  // Обработка события присоединения к комнате
  private onJoin(message: IMessage) {
    const obj = JSON.parse(message.body); // Парсим сообщение
    this.gameRoom = GameRoom.fromObject(obj); // Обновляем информацию о комнате
    console.log('Обновленная информация о комнате:', this.gameRoom); // Логируем обновленную информацию о комнате
  }

  // Метод для начала игры
  startGame() {
    // Проверяем, что gameRoom существует и есть соперник
    if (!!this.gameRoom && this.gameRoom?.opponentLogin) {
      // Запускаем игровую сессию через сервис
      this.gamePlayService.startGameplay(this.gameRoom.id).subscribe(
        response => {
          console.log('Игра создана:', response); // Логируем успешное создание игры
        },
        (error) => {
          console.error('Ошибка при создании игры:', error); // Логируем ошибку создания игры
          this.openSnackBar('Ошибка при создании игры: ' + error.error.message, 'Close'); // Показываем сообщение об ошибке
        }
      );
    } else {
      console.error('gameRoom is null or undefined или нет соперника'); // Логируем, если gameRoom не существует или нет соперника
    }
  }

  // Метод для отмены игры
  cancelGame() {
    // Проверяем, что gameRoom существует перед закрытием
    if (this.gameRoom) {
      this.gameRoomService.closeRoom(this.gameRoom.id).subscribe(
        response => {
          console.log('Комната закрыта:', response); // Логируем успешное закрытие комнаты
        },
        (error) => {
          console.error('Ошибка при закрытии комнаты:', error); // Логируем ошибку закрытия комнаты
          this.openSnackBar('Ошибка при закрытии игры: ' + error.error.message, 'Close'); // Показываем сообщение об ошибке
        }
      );
    } else {
      console.error('Не удалось закрыть комнату: gameRoom не существует'); // Логируем, если gameRoom не существует
    }
  }

  // Метод для отображения сообщения в Snackbar
  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }
}
