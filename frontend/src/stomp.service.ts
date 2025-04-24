import {Injectable} from '@angular/core';
import {CompatClient, Stomp} from '@stomp/stompjs';
import {Subject} from 'rxjs';
import {wsUrl} from './data.service';

@Injectable({
  providedIn: 'root'
})
export class StompService {
  public stompClient: CompatClient;
  private messagesSubject: Subject<any> = new Subject<any>();

  constructor() {
    this.stompClient = Stomp.over(new WebSocket(wsUrl));
    this.stompClient.connect({}, this.onConnected,  this.onError);
  }

  onConnected = () => {
    console.log("connected");

  };

  onError = () => {
    console.log("connect error");
  }


  public sendMessage(destination: string, message: any): void {
    this.stompClient.send(destination, {}, JSON.stringify(message));
  }

  public getMessages() {
    return this.messagesSubject.asObservable();
  }
}
