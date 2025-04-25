package org.revature.Alcott_P1_Backend.exception;

public class InvalidSessionException extends Exception{
    public InvalidSessionException(String errorMessage){
        super(errorMessage);
    }
}
