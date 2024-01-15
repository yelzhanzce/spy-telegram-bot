package org.telegram.spybot;

import org.telegram.spybot.bot.SpyGameBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class SpyGameBotStarter {
    public static void main(String[] args) throws TelegramApiException {
        var bot = new SpyGameBot();
        var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
