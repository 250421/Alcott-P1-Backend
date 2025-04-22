package org.revature.Alcott_P1_Backend.exception;

public class InvalidUsernameOrPasswordException extends Exception{
    public InvalidUsernameOrPasswordException(String errorMessage){
        super(errorMessage);
    }
}
