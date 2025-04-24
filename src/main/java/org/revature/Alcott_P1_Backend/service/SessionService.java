package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    public boolean endSession(){
        return false;
    }
}
