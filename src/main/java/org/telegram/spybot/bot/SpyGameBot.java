package org.telegram.spybot.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.spybot.dto.UserDto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.telegram.spybot.utils.AsyncExecutor.sendInAsync;
import static org.telegram.spybot.utils.SpyBotUtils.SPY_TEXT_RU;
import static org.telegram.spybot.utils.SpyBotUtils.getRandomCountry;

@Slf4j
public class SpyGameBot extends AbstractSpyGameBot {

    private boolean isPlaying = false;

    protected void onAbstractUpdateReceived(Update update) {
        var message = new SendMessage();
        var username = update.getMessage().getFrom().getUserName();

        if (update.hasMessage() && update.getMessage().hasText()) {
            var command = update.getMessage().getText();

            switch (command) {
                case "/plus" -> commandForPlus(message, update);
                case "/ready", ".r" -> commandForReady(username, message);
                case "/unready", ".ur" -> commandForUnready(username);
                case "/start" -> commandForStart(message);
                case "/minus" -> commandForMinus(username);
                case "/list" -> commandForList(message);
                case "/gg" -> commandForGG();
                default -> commandForDefault(message);
            }

            message.setChatId(update.getMessage().getChatId());
            if (message.getText().isEmpty()) {
                message.setText("404...");
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("error : {}", e.getMessage());
            }
        }
    }

    private static void commandForDefault(SendMessage message) {
        message.setText("there is no such command!");
    }

    private void commandForMinus(String username) {
        plusUsers.remove(username);
    }

    private void commandForUnready(String username) {
        readyUsers.remove(username);
    }

    private void commandForGG() {
        readyUsers.clear();
        isPlaying = false;
        sendInAsync(this::sendMessageToAllThatGameIsOver, this);
    }

    private void commandForList(SendMessage message) {
        if (plusUsers.isEmpty()) {
            message.setText("players list is empty!");
        } else {
            message.setText(getUserList());
        }
    }

    private void commandForStart(SendMessage message) {
        if (plusUsers.size() != readyUsers.size()) {
            var readyCheck = readyUsers.size() + "/" + plusUsers.size();
            message.setText("Not everyone is ready(" + readyCheck + ")");
        }
    }

    private void commandForReady(String username, SendMessage message) {
        if (plusUsers.containsKey(username)) {
            if (!readyUsers.contains(username)) {
                readyUsers.add(username);
                message.setText(getUserList());

                if (plusUsers.size() != 1 && plusUsers.size() == readyUsers.size()) {
                    isPlaying = true;

                    sendInAsync(this::sendMessages, this);
                }
            }
        } else {
            message.setText("You're not in list of players! Send /plus - to add");
        }
    }

    private void commandForPlus(SendMessage message, Update update) {
        var chatId = update.getMessage().getChatId();
        var firstName = update.getMessage().getChat().getFirstName();
        var username = update.getMessage().getFrom().getUserName();

        var user = new UserDto();
        user.setChatId(chatId);
        user.setFirstName(firstName);
        user.setUsername(username);

        if (isPlaying) {
            message.setText("the game is on");
        } else {
            plusUsers.put(username, user);
            message.setText(getUserList());
        }
    }

    private String getUserList() {
        var userLists = new StringBuilder();
        var i = new AtomicInteger(1);
        plusUsers.forEach((k, v) -> {
            var userName = v.getUsername();
            var isReadyText = readyUsers.contains(userName) ? "ready" : "not ready";
            var userList = i + ". @" + userName + "(" + isReadyText + ")" + "\n";
            i.incrementAndGet();

            userLists.append(userList);
        });

        return userLists.toString();
    }


    private Set<Long> getChatIds() {
        Set<Long> chatIds = new HashSet<>();
        for (Map.Entry<String, UserDto> stringUserEntry : plusUsers.entrySet()) {
            var chatId = stringUserEntry.getValue().getChatId();
            chatIds.add(chatId);
        }
        return chatIds;
    }

    private Long getRandomSpyId() {
        var chatIds = getChatIds();
        var longs = new ArrayList<>(chatIds);
        return longs.get(random.nextInt(longs.size()));
    }

    private void sendMessages(TelegramLongPollingBot bot) {
        var spyId = getRandomSpyId();
        var countryName = getRandomCountry(random);
        var chatIds = getChatIds();

        for (var chatId : chatIds) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            if (chatId.equals(spyId)) {
                sendMessage.setText(SPY_TEXT_RU);
            } else {
                sendMessage.setText(countryName);
            }

            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("error: {}", e.getMessage());
            }
        }
    }

    private void sendMessageToAllThatGameIsOver(TelegramLongPollingBot bot) {
        var chatIds = getChatIds();

        for (var chatId : chatIds) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText("the game is over");

            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("error: {}", e.getMessage());
            }
        }
    }
}