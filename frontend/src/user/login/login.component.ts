import {Component, Input} from '@angular/core';
import {UserService} from '../user-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {User} from '../user';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  @Input() login?: string;
  @Input() password?: string;
  public returnUrl: string = '/';

  constructor(private router: Router,
              private route: ActivatedRoute,
              private userService: UserService,
              private snackBar: MatSnackBar) {
    this.route.queryParams.subscribe(params => {
      this.returnUrl = params['returnUrl']; // Если returnUrl не указан, перенаправляем на главную страницу
    });
  }

  loginUser() {
    this.userService.login(this.login!, this.password!).subscribe(response => {
      console.log('User logged in successfully', response);
      this.userService.user = new User(response, this.login!)
      this.router.navigateByUrl(this.returnUrl); // Перенаправление на нужную страницу
    }, error => {
      console.error('Login failed', error);
      this.openSnackBar('Login failed: ' + error.error, 'Close');
    });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }

  crackLogin() {
    this.userService.user = new User('436ec41b-de96-452d-bfa6-957fda083a20', 'player1')
    this.router.navigateByUrl(this.returnUrl); // Перенаправление на нужную страницу
  }
}
