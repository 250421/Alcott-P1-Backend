package org.revature.Alcott_P1_Backend.repository;

import org.revature.Alcott_P1_Backend.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
}
