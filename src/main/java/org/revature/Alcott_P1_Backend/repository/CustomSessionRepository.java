package org.revature.Alcott_P1_Backend.repository;

import jakarta.transaction.Transactional;
import org.revature.Alcott_P1_Backend.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomSessionRepository extends JpaRepository<Session, Integer> {

    @Transactional
    long deleteBysessionId(String sessionId);

    boolean existsBysessionId(String session_id);

    Session findBysessionId(String session_id);
}
