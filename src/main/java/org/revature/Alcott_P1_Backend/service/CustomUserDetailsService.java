package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        return new User(
                account.getUsername(),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(account.getRole()))
        );
    }
}
