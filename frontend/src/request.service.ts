import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {UserService} from './user/user-service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  constructor(private http: HttpClient,
              private userService: UserService) {
  }

  post<T>(url: string, body: any, params: HttpParams = new HttpParams()) {
    return this.http.post<T>(url, body, {params: params, headers: this.getHeaders()})
  }

  get<T>(url: string, params: HttpParams = new HttpParams(), responseType: any = 'json'): Observable<T> {
    // Выполните GET-запрос с параметрами и заголовками
    return this.http.get<T>(url, { params: params, headers: this.getHeaders(), responseType: responseType });
  }
  put<T>(url: string, body: any, params: HttpParams = new HttpParams()) {
    return this.http.put<T>(url, body, {params: params, headers: this.getHeaders()})
  }

  delete<T>(url: string, params: HttpParams = new HttpParams()) {
    return this.http.delete<T>(url, {params: params, headers: this.getHeaders()})
  }

  private getHeaders() {
    return new HttpHeaders()
      .set('Authorization', 'Basic ' + this.encodeToBase64(`${this.userService.user?.login}:${this.userService.user?.password}`));
  }

  private encodeToBase64(str: string): string {
    // Преобразуем строку в массив байтов
    const bytes = new TextEncoder().encode(str);
    // Преобразуем массив байтов в base64
    return btoa(String.fromCharCode(...bytes));
  }
}

