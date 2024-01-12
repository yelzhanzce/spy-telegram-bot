package org.telegram.spybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class AbstractSpyGameBot extends TelegramLongPollingBot {

    protected Random random = new Random();
    protected Map<String, User> plusUsers = new HashMap<>();
    protected Set<String> readyUsers = new HashSet<>();

    protected List<String> countries = List.of(
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
}
