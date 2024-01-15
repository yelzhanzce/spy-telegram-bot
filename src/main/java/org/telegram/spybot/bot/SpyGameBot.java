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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.telegram.spybot.utils.SpyBotUtils.SPY_TEXT_RU;
import static org.telegram.spybot.utils.SpyBotUtils.getRandomCountry;

@Slf4j
public class SpyGameBot extends AbstractSpyGameBot {

    private boolean isPlaying = false;

    protected void onAbstractUpdateReceived(Update update) {
        var message = new SendMessage();

        if (update.hasMessage() && update.getMessage().hasText()) {
            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            var firstName = update.getMessage().getChat().getFirstName();
            var username = update.getMessage().getFrom().getUserName();

            var user = new UserDto();
            user.setChatId(chatId);
            user.setFirstName(firstName);
            user.setUsername(username);

            switch (messageText) {
                case "/plus" -> {
                    if (isPlaying) {
                        message.setText("the game is on");
                    } else {
                        plusUsers.put(username, user);
                        message.setText(getUserList());
                    }
                }
                case "/ready", ".r" -> {
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
                case "/unready", ".ur" -> readyUsers.remove(username);
                case "/start" -> {
                    if (plusUsers.size() != readyUsers.size()) {
                        var readyCheck = readyUsers.size() + "/" + plusUsers.size();
                        message.setText("Not everyone is ready(" + readyCheck + ")");
                    }
                }
                case "/minus" -> plusUsers.remove(username);
                case "/list" -> {
                    if (plusUsers.isEmpty()) {
                        message.setText("players list is empty!");
                    } else {
                        message.setText(getUserList());
                    }
                }
                case "/gg" -> {
                    readyUsers.clear();
                    isPlaying = false;
                    sendInAsync(this::sendMessageToAllThatGameIsOver, this);
                }
                default -> message.setText("there is no such command!");
            }

            message.setChatId(String.valueOf(chatId));
            if (message.getText().isEmpty()) {
                message.setText("404...");
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("error : {}", e);
            }
        }
    }

    private <T> void sendInAsync(Consumer<T> consumer, T accept) {
        var scheduler = Executors.newScheduledThreadPool(1);
        var future = new CompletableFuture<Void>();
        scheduler.schedule(() -> {
            consumer.accept(accept);
            future.complete(null);
        }, 1, TimeUnit.SECONDS);

        future.thenRun(() -> log.info("The method is executed"));
        scheduler.shutdown();
    }

    private String getUserList() {
        var userLists = new StringBuilder();
        var i = new AtomicInteger(1);
        plusUsers.forEach((k, v) -> {

            var userName = v.getUsername();
            var isReadyText = readyUsers.contains(userName) ? "isReady" : "notReady";
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

        for (Long chatId : chatIds) {
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
                e.printStackTrace();
            }
        }
    }

    private void sendMessageToAllThatGameIsOver(TelegramLongPollingBot bot) {
        var chatIds = getChatIds();

        for (Long chatId : chatIds) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText("the game is over");

            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}