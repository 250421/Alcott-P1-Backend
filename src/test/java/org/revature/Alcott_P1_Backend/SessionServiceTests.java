package org.revature.Alcott_P1_Backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.entity.Session;
import org.revature.Alcott_P1_Backend.exception.InvalidSessionException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.revature.Alcott_P1_Backend.repository.CustomSessionRepository;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.revature.Alcott_P1_Backend.service.SessionService;
import org.springframework.security.authentication.AuthenticationManager;

public class SessionServiceTests {

    private CustomSessionRepository sessionRepository;
    private SessionService sessionService;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        sessionRepository = mock(CustomSessionRepository.class);
        sessionService = new SessionService(sessionRepository, accountRepository);
    }
    
    @Test
void createNewSession_ShouldCreateSession_WhenValidInput() {
    Session session = new Session("sessionId123", new Account(), LocalDateTime.now());
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(false);

    boolean result = sessionService.createNewSession(session);

    assertTrue(result);
    verify(sessionRepository, times(1)).save(session);
}

@Test
void createNewSession_ShouldThrowException_WhenSessionIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sessionService.createNewSession(null));
}

@Test
void createNewSession_ShouldThrowException_WhenSessionIdIsNull() {
    Session session = new Session(null, new Account(), LocalDateTime.now());
    assertThrows(IllegalArgumentException.class, () -> sessionService.createNewSession(session));
}

@Test
void createNewSession_ShouldThrowException_WhenSessionAlreadyExists() {
    Session session = new Session("sessionId123", new Account(), LocalDateTime.now());
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> sessionService.createNewSession(session));
}

@Test
void deleteBySessionId_ShouldDeleteSession_WhenValidSessionId() throws InvalidSessionException {
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(true);
    when(sessionRepository.deleteBysessionId("sessionId123")).thenReturn(1);

    Long result = sessionService.deleteBySessionId("sessionId123");

    assertEquals(1L, result);
    verify(sessionRepository, times(1)).deleteBysessionId("sessionId123");
}

@Test
void deleteBySessionId_ShouldThrowException_WhenSessionIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sessionService.deleteBySessionId(null));
}

@Test
void deleteBySessionId_ShouldThrowException_WhenSessionDoesNotExist() {
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(false);

    assertThrows(InvalidSessionException.class, () -> sessionService.deleteBySessionId("sessionId123"));
}

@Test
void doesSessionExist_ShouldReturnTrue_WhenSessionAndUsernameMatch() throws InvalidSessionException, InvalidUsernameOrPasswordException {
    Session session = new Session("sessionId123", new Account("testUser", "password123!", "USER" ), LocalDateTime.now());
    Account account = new Account("testUser", "password123!", "USER" );

    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(true);
    when(sessionRepository.findBysessionId("sessionId123")).thenReturn(session);
    when(accountRepository.existsByUsername("testUser")).thenReturn(true);
    when(accountRepository.findByUsername("testUser")).thenReturn(account);

    boolean result = sessionService.doesSessionExist("sessionId123", "testUser");

    assertTrue(result);
}

@Test
void doesSessionExist_ShouldThrowException_WhenSessionIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sessionService.doesSessionExist(null, "testUser"));
}

@Test
void findSessionById_ShouldReturnSession_WhenValidSessionId() {
    Session session = new Session("sessionId123", new Account(), LocalDateTime.now());
    when(sessionRepository.findBysessionId("sessionId123")).thenReturn(session);

    Session result = sessionService.findSessionById("sessionId123");

    assertNotNull(result);
    assertEquals("sessionId123", result.getSessionId());
}

@Test
void findSessionById_ShouldThrowException_WhenSessionIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sessionService.findSessionById(null));
}

@Test
void updateSession_ShouldUpdateSession_WhenValidInput() {
    Session session = new Session("sessionId123", new Account(), LocalDateTime.now());
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(true);

    boolean result = sessionService.updateSession(session);

    assertTrue(result);
    verify(sessionRepository, times(1)).save(session);
}

@Test
void updateSession_ShouldThrowException_WhenSessionIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sessionService.updateSession(null));
}

@Test
void updateSession_ShouldThrowException_WhenSessionDoesNotExist() {
    Session session = new Session("sessionId123", new Account(), LocalDateTime.now());
    when(sessionRepository.existsBysessionId("sessionId123")).thenReturn(false);

    assertThrows(IllegalStateException.class, () -> sessionService.updateSession(session));
}


}
