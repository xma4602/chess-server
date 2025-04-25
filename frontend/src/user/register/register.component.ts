import {Component, Input} from '@angular/core';
import {UserService} from '../user-service';
import {User} from '../user';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  @Input() login?: string;
  @Input() password?: string;

  constructor(private router: Router,
              private userService: UserService,
              private snackBar: MatSnackBar) {
  }

  register() {
    this.userService.register(this.login!, this.password!).subscribe(response => {
      this.userService.user = new User(response, this.login!)
      console.log('User registered successfully', response);
      this.router.navigate(['main']);
    }, error => {
      console.error('Registration failed', error);
      this.openSnackBar('Login failed: ' + error.message, 'Close');
    });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000, // Время отображения в миллисекундах
    });
  }
}
