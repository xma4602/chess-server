import {Injectable} from '@angular/core';
import {CompatClient, IMessage, messageCallbackType, Stomp} from '@stomp/stompjs';
import {wsConnect} from './data.service';

@Injectable({
  providedIn: 'root'
})
export class StompService {
  public stompClient: CompatClient;
  private isConnected: boolean = false; // Флаг для отслеживания состояния подключения

  constructor() {
    this.stompClient = Stomp.over(function () {
      return new WebSocket(wsConnect)
    });
  }

  public connect() {
    return new Promise<void>((resolve, reject) => {
      if (this.isConnected) {
        resolve(); // Если уже подключены, просто разрешаем
        return;
      }

      this.stompClient.connect({}, () => {
        console.log("Connected to WebSocket");
        this.isConnected = true; // Устанавливаем флаг подключения
        resolve();
      }, (error: any) => {
        console.log("Connection error:", error);
        reject(error);
      });
    });
  }

  public subscribe(destination: string, callback: messageCallbackType) {
   return this.connect().then(() => {
      this.stompClient.subscribe(destination, callback);
    }).catch((error) => {
      console.error("Failed to connect:", error);
    });
  }

  public sendMessage(destination: string, message: IMessage): void {
    this.stompClient.send(destination, {}, JSON.stringify(message));
  }
}
