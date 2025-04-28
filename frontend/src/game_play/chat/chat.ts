export class ChatMessage {
  constructor(
    public chatId: string,
    public userId: string,
    public userLogin: string,
    public message: string,
    public timestamp: string,
    public id: string = '',
  ) {
  }
  static fromObject(obj: {
    chatId: string;
    userId: string;
    userLogin: string;
    message: string;
    timestamp: string;
    id?: string;
  }): ChatMessage {
    return new ChatMessage(
      obj.chatId,
      obj.userId,
      obj.userLogin,
      obj.message,
      obj.timestamp,
      obj.id || ''
    );
  }
}
