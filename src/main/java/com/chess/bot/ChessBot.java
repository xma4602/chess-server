package com.chess.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class ChessBot extends TelegramLongPollingBot {

    private final static String WEB_APP_URL = "https://google.com/";
    private final static WebAppInfo WEB_APP_INFO = new WebAppInfo(WEB_APP_URL);

    public ChessBot() {
    }

    @Override
    public String getBotUsername() {
        return "ChessBotTest";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var userId = user.getId();
            if (msg.isCommand()) {
                switch (msg.getText()) {
                    case "/start" -> {
                        sendStartMessage(userId,
                                "Добро пожаловать, нажми на кнопку ниже, чтобы открыть приложение",
                                "Открыть приложение");
                    }
                }
            }
            return;
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendStartMessage(Long who, String what, String textForButton) {
        KeyboardButton button = KeyboardButton.builder()
                .text(textForButton)
                .webApp(WEB_APP_INFO)
                .build();

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();

        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .replyMarkup(keyboard)
                .build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        ChessBot bot = new ChessBot();
        botsApi.registerBot(bot);
        bot.sendText(837823253L, "Я проснулся!");
    }
}
