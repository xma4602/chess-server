import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../user';
import {UserService} from '../user-service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {AuthModule} from '../../app/auth.module';
import {UserAvatarComponent} from '../avatar/user-avatar.component';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css'],
  imports: [AuthModule,
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
    public router: Router
  ) {
  }

  ngOnInit(): void {
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

  saveUser(): void {
    const formData = new FormData();
    formData.append('login', this.user!.login);
    formData.append('password', this.user!.password || '');
    formData.append('rating', this.user!.rating.toString());
    formData.append('roles', this.user!.roles.join(','));

    if (this.selectedFile) {
      formData.append('avatar', this.selectedFile, this.selectedFile.name); // Добавляем файл
    }

    this.userService.updateUser(this.user!.id, formData).subscribe(user => {
      this.router.navigate(['/users']); // Перенаправление на список пользователей после сохранения
    });
  }

  onRoleChange(role: string) {
    const index = this.user!.roles.indexOf(role);
    if (index === -1) {
      this.user!.roles.push(role); // Добавить роль, если она не выбрана
    } else {
      this.user!.roles.splice(index, 1); // Удалить роль, если она уже выбрана
    }
  }
  isRoleSelected(role: string): boolean {
    return this.user?.roles.includes(role) || false; // Проверяем, выбрана ли роль
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
