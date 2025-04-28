package org.revature.Alcott_P1_Backend.exception;

public class DuplicateEntryException extends Exception{
    public DuplicateEntryException(String errorMessage){
        super(errorMessage);
    }
}
