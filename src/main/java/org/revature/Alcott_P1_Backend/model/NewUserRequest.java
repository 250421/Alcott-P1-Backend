package org.revature.Alcott_P1_Backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewUserRequest {
    public NewUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    private String username;
    private String password;

}
