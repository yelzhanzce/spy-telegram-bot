package org.telegram.spybot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public class SpyGameBot extends AbstractSpyGameBot {

    private boolean isPlaying = false;
    private static final String SPY_TEXT = "Шпион";

    protected void onAbstractUpdateReceived(Update update) {
        SendMessage message = new SendMessage();

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getChat().getFirstName();
            String username = update.getMessage().getFrom().getUserName();
            User user = new User();
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
                        String readyCheck = readyUsers.size() + "/" + plusUsers.size();
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
                e.printStackTrace();
            }
        }
    }

    private <T> void sendInAsync(Consumer<T> consumer, T accept) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.schedule(() -> {
            consumer.accept(accept);
            future.complete(null);
        }, 1, TimeUnit.SECONDS);

        future.thenRun(() -> log.info("The method is executed"));
        scheduler.shutdown();
    }

    private String getUserList() {
        StringBuilder userLists = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        plusUsers.forEach((k, v) -> {

            String userName = v.getUsername();
            String isReadyText = readyUsers.contains(userName) ? "isReady" : "notReady";
            String userList = i + ". @" + userName + "(" + isReadyText + ")" + "\n";
            i.incrementAndGet();
            userLists.append(userList);
        });

        return userLists.toString();
    }

    private String getRandomCountry() {
        return countries.get(random.nextInt(countries.size()));
    }

    private Set<Long> getChatIds() {
        Set<Long> chatIds = new HashSet<>();
        for (Map.Entry<String, User> stringUserEntry : plusUsers.entrySet()) {
            Long chatId = stringUserEntry.getValue().getChatId();
            chatIds.add(chatId);
        }
        return chatIds;
    }

    private Long getRandomSpyId() {
        Set<Long> chatIds = getChatIds();
        List<Long> longs = new ArrayList<>(chatIds);
        return longs.get(random.nextInt(longs.size()));
    }

    private void sendMessages(TelegramLongPollingBot bot) {
        Long spyId = getRandomSpyId();
        String countryName = getRandomCountry();
        Set<Long> chatIds = getChatIds();

        for (Long chatId : chatIds) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            if (chatId.equals(spyId)) {
                sendMessage.setText(SPY_TEXT);
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
        Set<Long> chatIds = getChatIds();

        for (Long chatId : chatIds) {
            SendMessage sendMessage = new SendMessage();
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