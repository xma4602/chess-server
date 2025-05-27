import {AfterViewInit, Component, EventEmitter, Input, Output} from '@angular/core';
import {UserService} from '../user-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {User} from '../user';

import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  @Input() login?: string;
  @Input() password?: string;
  public returnUrl: string = '/';
  dev: boolean = true;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private userService: UserService,
              private snackBar: MatSnackBar) {
    this.route.queryParams.subscribe(params => {
      this.returnUrl = params['returnUrl']; // Если returnUrl не указан, перенаправляем на главную страницу
    });
  }

  loginUser() {
    this.makeLogin(this.login!, this.password!);
  }

  private makeLogin(login: string, password: string) {
    this.userService.login(login, password).subscribe(
      (response: {
        id: string,
        login: string,
        rating: number,
        roles: string[]
      }) => {
        console.log('User logged in successfully', response);
        this.userService.user = new User(response.id, login, password, response.rating, response.roles)
        this.router.navigateByUrl(this.returnUrl); // Перенаправление на нужную страницу
      }, (error: HttpErrorResponse) => {
        console.error('Ошибка авторизации', error);
        this.openSnackBar('Ошибка авторизации', 'Close');
      });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }

  crackLogin1() {
    this.makeLogin('player1', 'password1')
  }

  crackLogin2() {
    this.makeLogin('player2', 'password2')
  }

  crackLoginAdmin() {
    this.makeLogin('admin', 'admin')
  }
}
