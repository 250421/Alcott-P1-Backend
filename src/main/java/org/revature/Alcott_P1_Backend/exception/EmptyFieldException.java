package org.revature.Alcott_P1_Backend.exception;

public class EmptyFieldException extends Exception{
    public EmptyFieldException(String errorMessage){
        super(errorMessage);
    }
}
