package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.entity.Session;
import org.revature.Alcott_P1_Backend.exception.InvalidSessionException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.revature.Alcott_P1_Backend.repository.CustomSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Objects;

@Service
public class SessionService {

    private CustomSessionRepository customSessionRepository;

    private AccountRepository accountRepository;

    @Autowired
    public SessionService(CustomSessionRepository customSessionRepository, AccountRepository accountRepository) {
        this.customSessionRepository = customSessionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public boolean createNewSession(Session session){
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getSessionId() == null || session.getAccount() == null) {
            throw new IllegalArgumentException("Session ID and Account cannot be null");
        }
        if (customSessionRepository.existsBysessionId(session.getSessionId())) {
            throw new IllegalStateException("Session with the same ID already exists");
        }
    
        try {
            customSessionRepository.save(session);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Long deleteBySessionId(String sessionId) throws InvalidSessionException {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (!customSessionRepository.existsBysessionId(sessionId)) {
            throw new InvalidSessionException("No session found with the given ID");
        }
    
        if (customSessionRepository.deleteBysessionId(sessionId) < 1) {
            throw new InvalidSessionException("Failed to delete session");
        }
        return 1L;
    }

    public boolean doesSessionExist(String sessionId, String username) throws InvalidSessionException, InvalidUsernameOrPasswordException {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    
        Session session = null;
        Account account = null;
    
        if (customSessionRepository.existsBysessionId(sessionId)) {
            session = customSessionRepository.findBysessionId(sessionId);
        } else {
            return false;
        }
    
        if (accountRepository.existsByUsername(username)) {
            account = accountRepository.findByUsername(username);
        } else {
            throw new InvalidUsernameOrPasswordException("User not found");
        }
    
        return Objects.equals(session.getAccount().getId(), account.getId());    
    }

    public Session findSessionById(String session_id){
        if (session_id == null || session_id.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        return customSessionRepository.findBysessionId(session_id);
    }

    public boolean updateSession(Session session){
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (!customSessionRepository.existsBysessionId(session.getSessionId())) {
            throw new IllegalStateException("Session does not exist");
        }
    
        customSessionRepository.save(session);
        return true;
    }

}
