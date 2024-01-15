package org.telegram.spybot.bot;

import org.telegram.spybot.dto.UserDto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class AbstractSpyGameBot extends TelegramLongPollingBot {
    private static final String SPY_BOT_LINK = "@spykzbot";
    private static final String SPY_BOT_TOKEN = "6750136318:AAFcUUseY1CTIiYWDyuwro9qh_OoQRGK0hM";

    protected Random random = new Random();
    protected Map<String, UserDto> plusUsers = new HashMap<>();
    protected Set<String> readyUsers = new HashSet<>();

    protected AbstractSpyGameBot() {
        super(SPY_BOT_TOKEN);
    }

    @Override
    public String getBotUsername() {
        return SPY_BOT_LINK;
    }

    @Override
    public void onUpdateReceived(Update update) {
        onAbstractUpdateReceived(update);
    }

    protected abstract void onAbstractUpdateReceived(Update update);
}
