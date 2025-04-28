package org.revature.Alcott_P1_Backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationDTO {
    public String username;
    public String session;
    public String role;

}
