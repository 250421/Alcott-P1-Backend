package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.NewUserRequest;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AccountService(AccountRepository accountRepository, AuthenticationManager authenticationManager){
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
    }

    public Account createNewUser(NewUserRequest account) throws DuplicateUsernameException, InvalidUsernameOrPasswordException {
        if (account == null) {
            throw new IllegalArgumentException("Account request cannot be null");
        }
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            throw new InvalidUsernameOrPasswordException("Username cannot be null or empty");
        }
        if (account.getPassword() == null || account.getPassword().isEmpty()) {
            throw new InvalidUsernameOrPasswordException("Password cannot be null or empty");
        }
        if (!account.getUsername().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidUsernameOrPasswordException("Username must be a valid email address");
        }
        if (accountRepository.existsByUsername(account.getUsername().toLowerCase())) {
            throw new DuplicateUsernameException("Username already exists");
        }
        if (!account.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,25}$")) {
            throw new InvalidUsernameOrPasswordException("Password must be between 8 and 25 characters and include both a number and special character (@$!%*#?&)");
        }
    
        account.setPassword(encoder.encode(account.getPassword()));
    
        return accountRepository.save(new Account(
            account.getUsername().toLowerCase(), account.getPassword(), "USER"
        ));
    }

    public Account login(String username, String password) throws InvalidUsernameOrPasswordException {
        // checks
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameOrPasswordException("Username cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidUsernameOrPasswordException("Password cannot be null or empty");
        }
    
        String encryptedPassword = "";
        if (accountRepository.existsByUsername(username.toLowerCase())) {
            encryptedPassword = accountRepository.findByUsername(username.toLowerCase()).getPassword();
        } else {
            throw new InvalidUsernameOrPasswordException("Username not found");
        }
    
        if (encoder.matches(password, encryptedPassword)) {
            return accountRepository.findByUsernameAndPassword(username.toLowerCase(), encryptedPassword);
        } else {
            throw new InvalidUsernameOrPasswordException("Invalid username or password");
        }
    }

    public Account getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return accountRepository.findByUsername(username.toLowerCase());
    }

    public Authentication authenticateUser(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
    
        return authenticationManager.authenticate(authToken);
    }

}
