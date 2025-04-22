package org.revature.Alcott_P1_Backend.exception;

public class DuplicateUsernameException extends Exception{
    public DuplicateUsernameException(String errorMessage){
        super(errorMessage);
    }
}
