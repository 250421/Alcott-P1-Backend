package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.NewUserRequest;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account createNewUser(NewUserRequest account) throws DuplicateUsernameException, InvalidUsernameOrPasswordException {
        // checks
        // TODO: check for .com.com invalid username
        if(!account.getUsername().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            throw new InvalidUsernameOrPasswordException("Username must be a valid email address");
        }
        if(accountRepository.existsByUsername(account.getUsername().toLowerCase()))
            throw new DuplicateUsernameException("Username already exists");
        // Password must be between 8 and 25 characters and contain both a symbol and a number
        if(!account.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,25}$"))
            throw new InvalidUsernameOrPasswordException("Password must be between 8 and 25 characters and include both a number and special character (@$!%*#?&)");

        account.setPassword(encoder.encode(account.getPassword()));

        return accountRepository.save(new Account(
            account.getUsername(), account.getPassword(), "USER"
        ));
    }

    public Account login(String username, String password) throws InvalidUsernameOrPasswordException {
        // checks
        if(username.isEmpty() || password.isEmpty()){
            throw new InvalidUsernameOrPasswordException("Username and password fields must not be empty");
        }

        String encryptedPassword = "";
        if(accountRepository.existsByUsername(username.toLowerCase())) {
            encryptedPassword = accountRepository.findByUsername(username).getPassword();
        }
        else throw new InvalidUsernameOrPasswordException("Username not found");

        if(encoder.matches(password, encryptedPassword))
            return accountRepository.findByUsernameAndPassword(username, encryptedPassword);
        else throw new InvalidUsernameOrPasswordException("Invalid username or password");
    }

    public Account getUserByUsername(String username){
        return accountRepository.findByUsername(username);
    }

}
