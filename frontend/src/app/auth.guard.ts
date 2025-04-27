import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import {UserService} from '../user/user-service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private userService: UserService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    if (this.userService.isLoggedIn()) { // Проверьте, авторизован ли пользователь
      return true;
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } }); // Перенаправьте на страницу авторизации с сохранением URL
      return false;
    }
  }
}
