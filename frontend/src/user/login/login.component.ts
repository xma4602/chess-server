import {Component, Input} from '@angular/core';
import {UserService} from '../user-service';
import {Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
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

  constructor(private router: Router,
              private userService: UserService,
              private snackBar: MatSnackBar) {}

  loginUser() {
    this.userService.login(this.login!, this.password!).subscribe(response => {
      console.log('User logged in successfully', response);
      this.userService.user = new User(response, this.login!)
      this.router.navigate(['main']);
    }, error => {
      console.error('Login failed', error);
      this.openSnackBar('Login failed: ' + error.message, 'Close');
    });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }

  crackLogin() {
    this.userService.user = new User('test_id', 'test_login')
    this.router.navigate(['main']);
  }
}
