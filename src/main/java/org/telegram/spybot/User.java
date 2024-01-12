package org.telegram.spybot;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String firstName;
    private String username;
    private Long chatId;
}
