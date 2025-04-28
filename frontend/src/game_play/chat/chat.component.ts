import {Component, Input, OnInit} from '@angular/core';
import {UserService} from '../../user/user-service';
import {NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {StompService} from '../../stomp.service';
import {ChatMessage} from './chat';
import {wsChat} from '../../data.service';
import {ChatService} from './chat-service';

@Component({
  selector: 'app-chat',
  standalone: true,
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
  imports: [NgForOf, FormsModule]
})
export class ChatComponent implements OnInit {
  @Input() chatId: string = '';
  messages: ChatMessage[] = [] as ChatMessage[];
  newMessage: string = '';

  constructor(private userService: UserService,
              private charService: ChatService,
              private stompService: StompService
  ) {
  }

  ngOnInit() {
    if (this.chatId) {
      this.charService.getMessages(this.chatId).subscribe(
        msg => this.messages = msg,
        console.error
      )
    }

    this.stompService.subscribe(`${wsChat}/${this.chatId}/new`, message => {
      const obj = JSON.parse(message.body);
      const chatMessage = ChatMessage.fromObject(obj)
      this.messages.push(chatMessage)
      this.messages.sort((a, b) => {
        return new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime();
      });
    })
  }

  // Метод для отправки сообщения
  sendMessage() {
    if (this.newMessage.trim()) {
      this.charService.sendMessage(this.chatId, this.newMessage).subscribe(
        () =>  this.newMessage = '', // Очистка поля ввода
        console.error
      )
    }
  }

}
