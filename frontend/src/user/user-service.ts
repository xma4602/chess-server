import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
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
    return this.http.post<any>(`${restUsers}/register`, {}, {params});
  }

  login(login: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set("login", login)
      .set("password", password)
    return this.http.post<any>(`${restUsers}/login`, {}, {params: params, headers: this.getHeaders(login, password)});
  }

  isLoggedIn() {
    return this.user !== null;
  }

  getUsers() {
    return this.http.get<any[]>(`${restUsers}/all`, {headers: this.getHeaders()})
      .pipe(map(obj => obj.map(item => User.fromObject(item))));
  }

  updateUser(userId: string, data: FormData): Observable<User> {
    return this.http.put<any>(`${restUsers}/${userId}/profile`, data, {
      headers: this.getHeaders(), // Убедитесь, что заголовки корректные
      reportProgress: true, // Если хотите отслеживать прогресс загрузки
    }).pipe(
      map(obj => User.fromObject(obj))
    );
  }


  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${restUsers}/${userId}`, {headers: this.getHeaders()});
  }

  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${restUsers}/roles`, {headers: this.getHeaders()});
  }

  private getHeaders(login = this.user?.login, password = this.user?.password) {
    return new HttpHeaders()
      .set('Authorization', 'Basic ' + this.encodeToBase64(`${login}:${password}`));
  }

  private encodeToBase64(str: string): string {
    // Преобразуем строку в массив байтов
    const bytes = new TextEncoder().encode(str);
    // Преобразуем массив байтов в base64
    return btoa(String.fromCharCode(...bytes));
  }
}
