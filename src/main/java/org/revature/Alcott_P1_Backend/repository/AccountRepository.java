package org.revature.Alcott_P1_Backend.repository;

import org.revature.Alcott_P1_Backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    /**
     * Auto-generates a query method to check if a specific account exists in the database.
     * @param username The username of the account to search for.
     * @return True is account exists. False otherwise.
     */
    public boolean existsByUsername(String username);

    /**
     * Auto-generates a query method to check if a specific account exists in the database.
     * @param username The username of the account to search for.
     * @param password the password of the account to search for.
     * @return True is account exists. False otherwise.
     */
    public boolean existsByUsernameAndPassword(String username, String password);

    /**
     * Auto-generates a query method to check if a specific account exists in the database.
     * @param username The username of the account to search for.
     * @param password the password of the account to search for.
     * @return The account if it exists.
     */
    public Account findByUsernameAndPassword(String username, String password);
}
