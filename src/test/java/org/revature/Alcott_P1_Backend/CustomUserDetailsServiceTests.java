package org.revature.Alcott_P1_Backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.repository.AccountRepository;
import org.revature.Alcott_P1_Backend.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTests {

    private AccountRepository accountRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        customUserDetailsService = new CustomUserDetailsService(accountRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUsernameExists() {
        // Arrange
        Account account = new Account("test@example.com", "encodedPassword", "USER");
        when(accountRepository.findByUsername("test@example.com")).thenReturn(account);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUsernameDoesNotExist() {
        // Arrange
        when(accountRepository.findByUsername("nonexistent@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUsernameIsNull() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername(null)
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUsernameIsEmpty() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("")
        );
    }
}
