import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {GameConditions} from '../game_conditions/game-conditions';
import {TileComponent} from './tile/tile.component';
import {Router, RouterModule} from '@angular/router';
import {UserService} from '../user/user-service';
import {User} from '../user/user';

@Component({
  selector: 'app-main',
  standalone: true,
  templateUrl: './main.component.html',
  imports: [RouterModule, NgForOf, TileComponent, NgIf],
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  gameConditionsList: GameConditions[] = [];
  myGame: GameConditions = new GameConditions(5, 3)

  currentUser: User;

  constructor(private router: Router, private userService: UserService) {
    this.gameConditionsList.push(new GameConditions(1, 0))
    this.gameConditionsList.push(new GameConditions(2, 1))
    this.gameConditionsList.push(new GameConditions(3, 0))
    this.gameConditionsList.push(new GameConditions(3, 2))
    this.gameConditionsList.push(new GameConditions(5, 0))
    this.gameConditionsList.push(new GameConditions(5, 3))
    this.gameConditionsList.push(new GameConditions(10, 0))
    this.gameConditionsList.push(new GameConditions(10, 5))
    this.gameConditionsList.push(new GameConditions(15, 10))
    this.gameConditionsList.push(new GameConditions(30, 0))
    this.gameConditionsList.push(new GameConditions(30, 20))

    this.currentUser = userService.user!;
  }

  // Метод для проверки, является ли пользователь администратором
  isAdmin(): boolean {
    return this.userService.user!.roles.includes('admin'); // Предполагается, что у вас есть метод для проверки ролей
  }

  // Метод для перенаправления на таблицу пользователей
  navigateToUserList(): void {
    this.router.navigate(['/users']); // Замените на ваш маршрут для таблицы пользователей
  }

  openProfile() {
    this.router.navigate([`/users/${this.userService.user!.id}/profile`]);
  }

  logout() {
    this.userService.user = null
    this.router.navigate(['login']);
  }
}
