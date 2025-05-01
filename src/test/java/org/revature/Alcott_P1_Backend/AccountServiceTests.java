package org.revature.Alcott_P1_Backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.NewUserRequest;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTests {

    private AccountRepository accountRepository;
    private AuthenticationManager authenticationManager;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        accountService = new AccountService(accountRepository, authenticationManager);
    }

    @Test
    void createNewUser_ShouldCreateUser_WhenValidInput()
            throws DuplicateUsernameException, InvalidUsernameOrPasswordException {
        NewUserRequest newUser = new NewUserRequest("test@example.com", "Password@123");
        when(accountRepository.existsByUsername("test@example.com")).thenReturn(false);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(new Account("test@example.com", "encodedPassword", "USER"));

        Account createdAccount = accountService.createNewUser(newUser);

        assertNotNull(createdAccount);
        assertEquals("test@example.com", createdAccount.getUsername());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createNewUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        NewUserRequest newUser = new NewUserRequest("test@example.com", "Password@123");
        when(accountRepository.existsByUsername("test@example.com")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> accountService.createNewUser(newUser));
    }

    @Test
    void createNewUser_ShouldThrowException_WhenAccountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> accountService.createNewUser(null));
    }

    @Test
    void createNewUser_ShouldThrowException_WhenUsernameIsInvalid() {
        NewUserRequest newUser = new NewUserRequest("", "Password@123");
        assertThrows(InvalidUsernameOrPasswordException.class, () -> accountService.createNewUser(newUser));
    }

    @Test
    void createNewUser_ShouldThrowException_WhenPasswordIsInvalid() {
        NewUserRequest newUser = new NewUserRequest("test@example.com", "short");
        assertThrows(InvalidUsernameOrPasswordException.class, () -> accountService.createNewUser(newUser));
    }

    @Test
    void login_ShouldReturnAccount_WhenValidCredentials() throws InvalidUsernameOrPasswordException {
        String username = "test@example.com";
        String password = "Password@123";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        when(accountRepository.existsByUsername(username)).thenReturn(true);
        when(accountRepository.findByUsername(username)).thenReturn(new Account(username, encodedPassword, "USER"));
        when(accountRepository.findByUsernameAndPassword(username, encodedPassword))
                .thenReturn(new Account(username, encodedPassword, "USER"));

        Account loggedInAccount = accountService.login(username, password);

        assertNotNull(loggedInAccount);
        assertEquals(username, loggedInAccount.getUsername());
    }

    @Test
    void login_ShouldThrowException_WhenUsernameIsNull() {
        assertThrows(InvalidUsernameOrPasswordException.class, () -> accountService.login(null, "Password@123"));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsNull() {
        assertThrows(InvalidUsernameOrPasswordException.class, () -> accountService.login("test@example.com", null));
    }

    @Test
    void login_ShouldThrowException_WhenUsernameDoesNotExist() {
        when(accountRepository.existsByUsername("nonexistent@example.com")).thenReturn(false);
        assertThrows(InvalidUsernameOrPasswordException.class,
                () -> accountService.login("nonexistent@example.com", "Password@123"));
    }

    @Test
    void login_ShouldThrowException_WhenInvalidPassword() {
        String username = "test@example.com";
        String password = "WrongPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode("Password@123");

        when(accountRepository.existsByUsername(username)).thenReturn(true);
        when(accountRepository.findByUsername(username)).thenReturn(new Account(username, encodedPassword, "USER"));

        assertThrows(InvalidUsernameOrPasswordException.class, () -> accountService.login(username, password));
    }

    @Test
    void getUserByUsername_ShouldThrowException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> accountService.getUserByUsername(null));
    }

    @Test
    void getUserByUsername_ShouldReturnNull_WhenUsernameDoesNotExist() {
        when(accountRepository.findByUsername("nonexistent@example.com")).thenReturn(null);
        Account account = accountService.getUserByUsername("nonexistent@example.com");
        assertNull(account);
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> accountService.authenticateUser(null, "Password@123"));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> accountService.authenticateUser("test@example.com", null));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenAuthenticationFails() {
        String username = "test@example.com";
        String password = "Password@123";

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> accountService.authenticateUser(username, password));
    }
}
