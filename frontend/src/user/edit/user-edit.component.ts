import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../user';
import {UserService} from '../user-service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

import {UserAvatarComponent} from '../avatar/user-avatar.component';
import {ConfirmDialogComponent} from '../../game_play/dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    NgIf, UserAvatarComponent
  ],
})
export class UserEditComponent implements OnInit {
  user: User | null = null;
  roles: string[] = []; // Массив для хранения ролей
  selectedFile: File | null = null; // Для хранения загруженного файла

  constructor(
    public userService: UserService,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    public router: Router
  ) {
  }

  ngOnInit(): void {
    if (!this.userService.user!.roles.includes('admin')) {
      this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: 'У вас нет доступа к этой странице',
          message: 'Вы будете перенаправлены на начальный экран'
        }
      }).afterClosed().subscribe(
        () => this.router.navigate(['main']), console.error
      )
    } else {
      const userId = this.route.snapshot.paramMap.get('id');
      this.userService.getRoles().subscribe(
        (data) => {
          this.roles = data; // Сохраняем полученные роли
        },
        (error) => {
          console.error('Ошибка при получении ролей', error);
        }
      );
      if (userId) {
        this.userService.getUsers().subscribe(users => {
          this.user = users.find(u => u.id === userId) || null;
        });
      }
    }
  }

  saveUser(): void {
    const formData = new FormData();
    formData.append('login', this.user!.login);
    formData.append('password', this.user!.password);
    formData.append('rating', this.user!.rating.toString());
    this.user!.roles.forEach(role => formData.append('roles', role))

    if (this.selectedFile) {
      formData.append('avatar', this.selectedFile, this.selectedFile.name); // Добавляем файл
    }

    formData.forEach((value, key) => {
      console.log(key, value);
    });
    this.userService.updateUser(this.user!.id, formData).subscribe(user => {
      this.router.navigate(['/users']); // Перенаправление на список пользователей после сохранения
    });
  }

  onRoleChange(role: string) {
    this.user!.roles = [role]; // Устанавливаем выбранную роль
  }

  isRoleSelected(role: string): boolean {
    return this.user?.roles[0] === role; // Проверяем, является ли роль выбранной
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Сохраняем загруженный файл

      // Создаем URL для нового изображения
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.user!.avatarUrl = e.target.result; // Обновляем URL аватара
      };
      reader.readAsDataURL(this.selectedFile); // Читаем файл как Data URL
    }
  }
}
