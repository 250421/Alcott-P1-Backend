package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.entity.Session;
import org.revature.Alcott_P1_Backend.exception.InvalidSessionException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.AuthenticationDTO;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.revature.Alcott_P1_Backend.repository.CustomSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SessionService {

    private CustomSessionRepository customSessionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    public SessionService(CustomSessionRepository customSessionRepository){
        this.customSessionRepository = customSessionRepository;
    }

    public boolean createNewSession(Session session){
        customSessionRepository.save(session);
        // TODO: If persists...
        return true;
    }

    public Long deleteBySessionId(String sessionId) throws InvalidSessionException {
        if(customSessionRepository.deleteBysessionId(sessionId) < 1)
            throw new InvalidSessionException("No session found");
        else
            return 1L;
    }

    public boolean doesSessionExist(String sessionId, String username) throws InvalidSessionException, InvalidUsernameOrPasswordException {
        Session session = null;
        Account account = null;
        // Check if session exists
        if(customSessionRepository.existsBysessionId(sessionId)){
            // Get session
            session = customSessionRepository.findBysessionId(sessionId);
        }
        else {
            throw new InvalidSessionException("Invalid Session");
        }

        //check if account exists
        if(accountRepository.existsByUsername(username)){
            account = accountRepository.findByUsername(username);
        }
        else{
            throw new InvalidUsernameOrPasswordException("User not found");
        }

        return Objects.equals(session.getAccount().getId(), account.getId());
    }

    public Session findSessionById(String session_id){
        return customSessionRepository.findBysessionId(session_id);
    }
}
