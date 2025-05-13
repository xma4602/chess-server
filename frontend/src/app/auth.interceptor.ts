import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {UserService} from '../user/user-service';
import {restUsers} from '../data.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  registerUrl = `${restUsers}/register`

  constructor(private userService: UserService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    if (request.url !== this.registerUrl){
      request = request.clone({
        setHeaders: {
          Authorization: 'Basic ' + btoa(`${this.userService.user!.login}:${this.userService.user!.password}`)
        }
      });
    }

    return next.handle(request);
  }
}
