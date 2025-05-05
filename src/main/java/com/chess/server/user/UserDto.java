package com.chess.server.user;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String login;
    private String password;
    private Integer rating;
    private List<String> roles;
    private byte[] avatar;
}
