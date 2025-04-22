package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account createNewUser(Account account) throws DuplicateUsernameException {

        if(accountRepository.existsByUsername(account.getUsername()))
            throw new DuplicateUsernameException("Duplicate username");
        // TODO: add some password regex here~

        return accountRepository.save(account);
    }

    public Account login(String username, String password) throws InvalidUsernameOrPasswordException {
        if(accountRepository.existsByUsernameAndPassword(username, password))
            return accountRepository.findByUsernameAndPassword(username, password);
        else throw new InvalidUsernameOrPasswordException("Invalid username or password");
    }

}
