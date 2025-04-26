import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {restGamePlay, restUrl} from '../data.service';
import {User} from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public user: User | null = null

  constructor(private http: HttpClient) {
  }

  register(login: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set("login", login)
      .set("password", password)
    return this.http.post(`${restUrl}/user/register`, {}, {params});
  }

  login(login: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set("login", login)
      .set("password", password)
    return this.http.post(`${restUrl}/user/login`, {}, {params});
  }

  isLoggedIn() {
    return this.user !== null;
  }
}
