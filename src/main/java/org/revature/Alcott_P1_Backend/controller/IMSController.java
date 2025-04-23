package org.revature.Alcott_P1_Backend.controller;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
public class IMSController {

    @Autowired
    AccountService accountService;

    /***
     * Used to test connection between frontend and backend
     * Currently unsure if it checks for auth
     * @return
     */
    @GetMapping("/custom")
    public ResponseEntity<Account> getUserbyUsername(){
        return ResponseEntity.ok(accountService.getUserByUsername("admin"));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody Account account) throws InvalidUsernameOrPasswordException, DuplicateUsernameException {
        return ResponseEntity.ok(accountService.createNewUser(account).getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) throws InvalidUsernameOrPasswordException {
        return ResponseEntity.ok(accountService.login(account.getUsername(), account.getPassword()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public String duplicateUsernameExceptionHandler(DuplicateUsernameException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidUsernameOrPasswordException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public String invalidUsernameOrPasswordExceptionHandler(InvalidUsernameOrPasswordException ex){
        return ex.getMessage();
    }

}
