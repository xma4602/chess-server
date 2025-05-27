import {Component} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {UserService} from '../user/user-service';
import {UserAvatarComponent} from '../user/avatar/user-avatar.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, UserAvatarComponent, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor(private router: Router, private userService: UserService) {
  }

  // Метод для проверки, является ли пользователь администратором
  isAdmin(): boolean {
    const currentRoute = this.router.url; // Получаем текущий URL
    return !currentRoute.includes('users')
      && this.userService.user!.roles.includes('admin'); // Предполагается, что у вас есть метод для проверки ролей
  }

  // Метод для перенаправления на таблицу пользователей
  navigateToUserList(): void {
    this.router.navigate(['/users']); // Замените на ваш маршрут для таблицы пользователей
  }

  openProfile() {
    if (this.isMain()) {
      this.router.navigate([`/users/${this.userService.user!.id}/profile`]);
    }
  }

  logout() {
    this.userService.user = null
    this.router.navigate(['login']);
  }

  getUser() {
    return this.userService.user!;
  }

  profileVisible(): boolean {
    const currentRoute = this.router.url; // Получаем текущий URL
    return !currentRoute.includes('login')
      && !currentRoute.includes('register')
      && !currentRoute.includes('profile');
  }

  isMain() {
    const currentRoute = this.router.url; // Получаем текущий URL
    return currentRoute.includes('main');
  }
}
