package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.repository.CustomSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private CustomSessionRepository customSessionRepository;

    @Autowired
    public SessionService(CustomSessionRepository customSessionRepository){
        this.customSessionRepository = customSessionRepository;
    }

    public boolean authenticate(){
        return false;
    }
}
