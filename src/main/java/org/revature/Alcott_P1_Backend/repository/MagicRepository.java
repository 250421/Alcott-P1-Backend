package org.revature.Alcott_P1_Backend.repository;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.entity.Magic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagicRepository extends JpaRepository<Magic, Integer> {

    public boolean existsByName(String name);

    public Magic findByName(String name);

}
