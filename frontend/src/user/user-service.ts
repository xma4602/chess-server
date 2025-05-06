import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {restUsers} from '../data.service';
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
    return this.http.post<any>(`${restUsers}/register`, {}, {params})
      .pipe(map(obj => User.fromObject(obj)));
  }

  login(login: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set("login", login)
      .set("password", password)
    return this.http.post<any>(`${restUsers}/login`, {}, {params})
      .pipe(map(obj => User.fromObject(obj)));
  }

  isLoggedIn() {
    return this.user !== null;
  }

  getUsers() {
    return this.http.get<any[]>(`${restUsers}/all`)
      .pipe(map(obj => obj.map(item => User.fromObject(item))));
  }

  updateUser(userId: string, data: FormData): Observable<User> {
    return this.http.put<User>(`${restUsers}/${userId}/profile`, data)
      .pipe(map(obj => User.fromObject(obj)));
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${restUsers}/${userId}`);
  }

  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${restUsers}/roles`);
  }

}
