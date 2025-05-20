import {Component, Input, OnInit} from '@angular/core';
import {User} from '../user';
import {GameHistory} from './game_history/game-history';
import {GameHistoryComponent} from './game_history/game-history.component';
import {NgForOf, NgIf} from '@angular/common';
import {GameHistoryService} from './game_history/game-history.service';
import {ActivatedRoute} from '@angular/router';
import {UserService} from '../user-service';
import {FormsModule} from '@angular/forms';

import {UserAvatarComponent} from '../avatar/user-avatar.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [
    GameHistoryComponent,
    NgForOf,
    NgIf,
    FormsModule, UserAvatarComponent
  ],
})
export class UserProfileComponent implements OnInit {
  @Input() user!: User; // Входной параметр для получения пользователя
  histories: GameHistory[] | null = null;
  isEditing: boolean = false; // Флаг для отслеживания режима редактирования
  coryUser: User; // Для хранения оригинальных данных пользователя
  selectedFile: File | null = null; // Для хранения загруженного файла

  constructor(private route: ActivatedRoute,
              private gameHistoryService: GameHistoryService,
              private userService: UserService) {
    this.user = userService.user!;
    this.coryUser = this.user.clone(); // Используем метод clone для сохранения оригинальных данных
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const userId = params.get('id')!; // Получаем userId из параметров маршрута

      this.gameHistoryService.getHistories(userId).subscribe(
        histories => {
          // Сортируем массив и присваиваем его свойству компонента
          this.histories = histories.sort((a, b) => {
            return this.parseDate(b.timestamp).getTime() - this.parseDate(a.timestamp).getTime();
          });
        }
      );
    });
  }

  parseDate(dateString: string): Date {
    const [datePart, timePart] = dateString.split(' ');
    const [day, month, year] = datePart.split('.').map(Number);
    const [hours, minutes] = timePart.split(':').map(Number);

    // Создаем объект Date
    return new Date(year, month - 1, day, hours, minutes); // Месяцы в JavaScript начинаются с 0
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Сохраняем загруженный файл

      // Создаем URL для нового изображения
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.coryUser.avatarUrl = e.target.result; // Обновляем URL аватара
      };
      reader.readAsDataURL(this.selectedFile); // Читаем файл как Data URL
    }
  }


  saveChanges(): void {
    const formData = new FormData();
    formData.append('login', this.coryUser.login);
    formData.append('password', this.coryUser.password || '');

    if (this.selectedFile) {
      formData.append('avatar', this.selectedFile, this.selectedFile.name); // Добавляем файл
    }

    this.userService.updateUser(this.coryUser.id, formData).subscribe(user => {
      this.isEditing = false; // Выход из режима редактирования
      this.userService.user = new User(
        this.coryUser.id,
        this.coryUser.login,
        this.coryUser.password,
        this.coryUser.rating,
        this.coryUser.roles,
      )
    });
  }

  cancelChanges(): void {
    // Отмена изменений
    this.user = this.coryUser.clone(); // Восстанавливаем оригинальные данные с помощью метода clone
    this.isEditing = false; // Выход из режима редактирования
  }
}
