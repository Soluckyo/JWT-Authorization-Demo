package org.lib.jwtdemo.security;

import lombok.Data;

@Data
public class UserCredentialsDTO {
    private String email;
    private String password;
}
