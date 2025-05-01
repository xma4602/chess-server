import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
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
    return this.http.post(`${restUsers}/register`, {}, {params});
  }

  login(login: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set("login", login)
      .set("password", password)
    return this.http.post(`${restUsers}/login`, {}, {params});
  }

  isLoggedIn() {
    return this.user !== null;
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${restUsers}/all`);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${restUsers}/${user.id}`, user);
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${restUsers}/${userId}`);
  }
  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${restUsers}/roles`);
  }

}
