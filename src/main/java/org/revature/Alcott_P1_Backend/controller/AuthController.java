package org.revature.Alcott_P1_Backend.controller;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin( origins = "http://localhost:8080")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<Account> createNewAccount(@RequestBody Account account) throws Exception{
        return ResponseEntity.ok(accountService.createNewUser(account));
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) throws Exception{
        return ResponseEntity.ok(accountService.login(account.getUsername(), account.getPassword()));
    }
}
