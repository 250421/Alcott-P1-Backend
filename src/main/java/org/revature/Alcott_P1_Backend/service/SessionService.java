package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Session;
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

    public boolean createNewSession(Session session){
        customSessionRepository.save(session);
        // TODO: If persists...
        return true;
    }

    public Long deleteBySessionId(String sessionId) throws Exception {
        if(customSessionRepository.deleteBysessionId(sessionId) < 1)
            throw new Exception("No session found");
        else
            return 1L;
    }
}
