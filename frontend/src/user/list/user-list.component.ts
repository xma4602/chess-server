import {Component, OnInit} from '@angular/core';
import {UserService} from '../user-service';
import {User} from '../user';
import {NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {ConfirmDialogComponent} from '../../game_play/dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';


@Component({
  selector: 'app-user-list',
  standalone: true,
  templateUrl: './user-list.component.html',
  imports: [
    NgForOf,
    NgIf
  ],
  styleUrls: ['./user-list.component.css'] // Подключаем стили
})
export class UserListComponent implements OnInit {
  users: User[] = [];

  public currentUser: User ;

  constructor(private router: Router,
              public dialog: MatDialog,
              private userService: UserService) {
    this.currentUser = userService.user!;
  }

  ngOnInit(): void {
    if (this.userService.user!.roles.includes('admin')) {
      this.loadUsers();
    } else {
      this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: 'У вас нет доступа к этой странице',
          message: 'Вы будете перенаправлены на начальный экран'
        }
      }).afterClosed().subscribe(
        () => this.router.navigate(['']), console.error
      )
    }
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(users => {
      this.users = users;
      this.users.sort((a, b) => a.login.localeCompare(b.login));
    });
  }

  editUser(user: User): void {
    // Здесь вы можете реализовать логику редактирования пользователя
    console.log('Edit user:', user);
    this.router.navigate([`/users/${user.id}`]); // Замените на ваш маршрут для таблицы пользователей
  }

  deleteUser(userId: string): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe(() => {
        this.loadUsers(); // Обновляем список пользователей после удаления
      });
    }
  }

}
