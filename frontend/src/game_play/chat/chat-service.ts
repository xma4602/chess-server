import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {ChatMessage} from './chat';
import {restChat} from '../../data.service';
import {UserService} from '../../user/user-service';
import {RequestService} from '../../request.service';
import {HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(private requestService :RequestService,
              private userService: UserService) {
  }

  getMessages(chatId: string): Observable<ChatMessage[]> {
    const params = new HttpParams()
      .set('chatId', chatId)
    return this.requestService.get<ChatMessage[]>(`${restChat}/${chatId}`, params)
      .pipe(map(array => array.map(m => ChatMessage.fromObject(m))));
  }

  sendMessage(chatId: string, message: string) {
    const userId = this.userService.user?.id!;
    const params = new HttpParams()
      .set('userId', userId)
    return this.requestService.post(`${restChat}/${chatId}`, message, params)
  }
}
