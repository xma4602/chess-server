import {Component, Input, OnInit} from '@angular/core';
import {User} from '../user';
import {GameHistory} from './game_history/game-history';
import {GameHistoryComponent} from './game_history/game-history.component';
import {NgForOf, NgIf} from '@angular/common';
import {GameHistoryService} from './game_history/game-history.service';
import {ActivatedRoute} from '@angular/router';
import {UserService} from '../user-service';
import {FormsModule} from '@angular/forms';
import {AuthModule} from '../../app/auth.module';
import {UserAvatarComponent} from '../avatar/user-avatar.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [AuthModule,
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
  originalUser: User; // Для хранения оригинальных данных пользователя
  selectedFile: File | null = null; // Для хранения загруженного файла

  constructor(private route: ActivatedRoute,
              private gameHistoryService: GameHistoryService,
              private userService: UserService) {
    this.user = userService.user!;
    this.originalUser = this.user.clone(); // Используем метод clone для сохранения оригинальных данных
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const userId = params.get('id')!; // Получаем userId из параметров маршрута
      this.gameHistoryService.getHistories(userId).subscribe(
        histories =>{
          histories.sort((a, b) => {
            return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
          });
          this.histories = histories
        }
      );
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Сохраняем загруженный файл

      // Создаем URL для нового изображения
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.user.avatarUrl = e.target.result; // Обновляем URL аватара
      };
      reader.readAsDataURL(this.selectedFile); // Читаем файл как Data URL
    }
  }


  saveChanges(): void {
    const formData = new FormData();
    formData.append('login', this.user.login);
    formData.append('password', this.user.password || '');
    if (this.selectedFile) {
      formData.append('avatar', this.selectedFile, this.selectedFile.name); // Добавляем файл
    }

    this.userService.updateUser(this.user.id, formData).subscribe(user => {
      this.isEditing = false; // Выход из режима редактирования
      this.userService.user = user
    });
  }

  cancelChanges(): void {
    // Отмена изменений
    this.user = this.originalUser.clone(); // Восстанавливаем оригинальные данные с помощью метода clone
    this.isEditing = false; // Выход из режима редактирования
  }
}
