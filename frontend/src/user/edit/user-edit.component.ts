import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../user';
import {UserService} from '../user-service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css'],
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
})
export class UserEditComponent implements OnInit {
  user: User | null = null;
  roles: string[] = []; // Массив для хранения ролей

  constructor(
    private userService: UserService,
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
    if (this.user) {
      this.userService.updateUser(this.user).subscribe(() => {
        this.router.navigate(['/users']); // Перенаправление на список пользователей после сохранения
      });
    }
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

}
