import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GamePlay} from '../game_play/game-play';
import {gameplayUrl, restUrl} from '../data.service';
import {User} from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public user: User | null = new User('user1', 'CreatorLogin')

  constructor(private http: HttpClient) {
  }

  register(login: string, password: string): Observable<any> {
    return this.http.post(`${restUrl}/register`, {login, password});
  }

  login(login: string, password: string): Observable<any> {
    return this.http.post(`${restUrl}/login`, {login, password});
  }
}
