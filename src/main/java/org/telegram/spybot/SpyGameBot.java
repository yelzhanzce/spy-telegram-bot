package org.telegram.spybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpyGameBot extends TelegramLongPollingBot {

    private Random random = new Random();
    private Map<String, User> plusUsers = new HashMap<>();
    private Set<String> readyUsers = new HashSet<>();

    private List<String> countries = List.of(
            "China",
            "India",
            "United States",
            "Indonesia",
            "Pakistan",
            "Brazil",
            "Nigeria",
            "Bangladesh",
            "Russia",
            "Mexico",
            "Japan",
            "Ethiopia",
            "Philippines",
            "Egypt",
            "Vietnam",
            "DR Congo",
            "Turkey",
            "Iran",
            "Germany",
            "Thailand",
            "United Kingdom",
            "France",
            "Italy",
            "Tanzania",
            "South Africa",
            "Myanmar",
            "Kenya",
            "South Korea",
            "Colombia",
            "Spain",
            "Uganda",
            "Argentina",
            "Algeria",
            "Sudan",
            "Ukraine",
            "Iraq",
            "Afghanistan",
            "Poland",
            "Canada",
            "Morocco",
            "Saudi Arabia",
            "Uzbekistan",
            "Peru",
            "Angola",
            "Malaysia",
            "Mozambique",
            "Ghana",
            "Yemen",
            "Nepal",
            "Venezuela",
            "Madagascar",
            "Cameroon",
            "Côte d'Ivoire",
            "North Korea",
            "Australia",
            "Niger",
            "Taiwan",
            "Sri Lanka",
            "Burkina Faso",
            "Mali",
            "Romania",
            "Malawi",
            "Chile",
            "Kazakhstan",
            "Zambia",
            "Guatemala",
            "Ecuador",
            "Syria",
            "Netherlands",
            "Senegal",
            "Cambodia",
            "Chad",
            "Somalia",
            "Zimbabwe",
            "Guinea",
            "Rwanda",
            "Benin",
            "Burundi",
            "Tunisia",
            "Bolivia",
            "Belgium",
            "Haiti",
            "Cuba",
            "South Sudan",
            "Dominican Republic",
            "Czech Republic",
            "Greece",
            "Jordan",
            "Portugal",
            "Azerbaijan",
            "Sweden",
            "Honduras",
            "United Arab Emirates",
            "Hungary",
            "Tajikistan",
            "Belarus",
            "Austria",
            "Papua New Guinea",
            "Serbia",
            "Israel",
            "Switzerland",
            "Togo",
            "Sierra Leone",
            "Laos",
            "Paraguay",
            "Bulgaria",
            "Libya",
            "Lebanon",
            "Nicaragua",
            "Kyrgyzstan",
            "El Salvador",
            "Turkmenistan",
            "Singapore",
            "Denmark",
            "Finland",
            "Slovakia",
            "Norway",
            "Oman",
            "State of Palestine",
            "Costa Rica"
    );


    @Override
    public String getBotUsername() {
        return "@spykzbot";
    }

    @Override
    public String getBotToken() {
        return "6750136318:AAFcUUseY1CTIiYWDyuwro9qh_OoQRGK0hM";
    }

    @Override
    public void onUpdateReceived(Update update) {
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

            if (messageText.equals("/plus")) {
                plusUsers.put(username, user);
                message.setText("Added: " + username);
            } else if (messageText.equals("/ready") || messageText.equals("/.r")) {
                if (plusUsers.keySet().contains(username)) {
                    if (!readyUsers.contains(username)) {
                        readyUsers.add(username);
                        message.setText(username + " is ready");

                        if (plusUsers.size() != 1 && plusUsers.size() == readyUsers.size()) {
                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                            // Создаем CompletableFuture
                            CompletableFuture<Void> future = new CompletableFuture<>();

                            // Запускаем задачу с задержкой 1 секунда
                            scheduler.schedule(() -> {
                                // Метод, который нужно вызвать
                                sendMessages(this, getRandomSpyId(), getRandomCountry());
                                // Завершаем CompletableFuture
                                future.complete(null);
                            }, 1, TimeUnit.SECONDS);

                            // Добавляем обработчик завершения для CompletableFuture
                            future.thenRun(() -> System.out.println("Метод выполнен"));

                            // Закрываем scheduler
                            scheduler.shutdown();
                        }
                    }
                } else {
                    message.setText("send /plus");
                }

            } else if (messageText.equals("/unready")) {
                readyUsers.remove(username);
            } else if (messageText.equals("/start")) {
                if (plusUsers.size() != readyUsers.size()) {
                    String readyCheck = readyUsers.size() + "/" + plusUsers.size();
                    message.setText("Not everyone is ready(" + readyCheck + ")");
                }
            } else if (messageText.equals("/minus")) {
                plusUsers.remove(username);
            } else if (messageText.equals("/list")) {
                StringBuilder userLists = new StringBuilder();
                AtomicInteger i = new AtomicInteger(1);
                plusUsers.forEach((k, v) -> {

                    String userName = v.getUsername();
                    String isReadyText = readyUsers.contains(userName) ? "isReady" : "notReady";
                    String userList = i + ". @" + userName + "(" + isReadyText + ")" + "\n";
                    i.incrementAndGet();
                    userLists.append(userList);
                });

                message.setText(userLists.toString());
            }

            message.setChatId(String.valueOf(chatId));
            if (message.getText().isEmpty()) {
                message.setText("Random fact");
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
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

    public void sendMessages(TelegramLongPollingBot bot, Long spyId, String countryName) {
        Set<Long> chatIds = getChatIds();

        for (Long chatId : chatIds) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            if (chatId.equals(spyId)) {
                sendMessage.setText("Шпион");
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
}