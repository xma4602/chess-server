import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {ChatMessage} from './chat';
import {restChat} from '../../data.service';
import {UserService} from '../../user/user-service';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(private http: HttpClient,
              private userService: UserService) {
  }

  getMessages(chatId: string): Observable<ChatMessage[]> {
    const params = new HttpParams()
      .set('chatId', chatId)
    return this.http.get<ChatMessage[]>(`${restChat}/${chatId}`, {params})
      .pipe(map(array => array.map(m => ChatMessage.fromObject(m))));
  }

  sendMessage(chatId: string, message: string) {
    const userId = this.userService.user?.id!;
    const params = new HttpParams()
      .set('userId', userId)
    return this.http.post(`${restChat}/${chatId}`, message, {params})
  }
}
