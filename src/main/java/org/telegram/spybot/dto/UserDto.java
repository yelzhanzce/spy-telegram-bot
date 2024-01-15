package org.telegram.spybot.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String username;
    private Long chatId;
}
