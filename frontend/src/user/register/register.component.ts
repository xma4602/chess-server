import {Component, Input} from '@angular/core';
import {UserService} from '../user-service';
import {User} from '../user';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [RouterLink, FormsModule]
})
export class RegisterComponent {
  @Input() login?: string;
  @Input() password?: string;
  private returnUrl: any;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private userService: UserService,
              private snackBar: MatSnackBar) {
    this.route.queryParams.subscribe(params => {
      this.returnUrl = params['returnUrl']; // Если returnUrl не указан, перенаправляем на главную страницу
    });
  }

  register() {
    this.userService.register(this.login!, this.password!).subscribe((response: {
      id: string,
      login: string,
      password: string,
      rating: number,
      roles: string[]
    }) => {
      this.userService.user = new User(response.id, response.login, response.password, response.rating, response.roles)
      console.log('User registered successfully', response);
      this.router.navigateByUrl(this.returnUrl); // Перенаправление на нужную страницу
    }, error => {
      console.error('Registration failed', error);
      this.openSnackBar('Login failed: ' + error.error, 'Close');
    });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }
}
