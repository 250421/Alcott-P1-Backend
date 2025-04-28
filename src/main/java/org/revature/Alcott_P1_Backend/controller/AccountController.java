package org.revature.Alcott_P1_Backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.entity.Session;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidSessionException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.NewUserRequest;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.revature.Alcott_P1_Backend.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://localhost:5173", allowCredentials = "true")
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    SessionService sessionService;


    @PostMapping("/sign-up")
    public ResponseEntity<String> registerNewUser(@RequestBody NewUserRequest newUser) throws InvalidUsernameOrPasswordException, DuplicateUsernameException {
        try {
            accountService.createNewUser(newUser);
            return ResponseEntity.status(201).body("User created successfully");
        }
        catch (InvalidUsernameOrPasswordException e){
            return ResponseEntity.status(400).body("Invalid username or password");
        }
        catch (DuplicateUsernameException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody NewUserRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = accountService.authenticateUser(request.getUsername(), request.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Create session
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "User is already logged in on current session"));
            }
            session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            String sessionId = session.getId();
            Account user = accountService.getUserByUsername(request.getUsername());

            Session dbSession = new Session();
            dbSession.setSessionId(sessionId);
            dbSession.setAccount(user);
            dbSession.setCreatedAt(LocalDateTime.now());
            dbSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));

            sessionService.createNewSession(dbSession);

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // Invalidating session
            HttpSession session = request.getSession(false);
            if (session != null) {
                String sessionId = session.getId();
                sessionService.deleteBySessionId(sessionId);

                session.invalidate();

                // Clearing Spring Security context
                SecurityContextHolder.clearContext();

                Cookie cookie = new Cookie("JSESSIONID", null);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0); // remove cookie
                response.addCookie(cookie);

                return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
            }
            else {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "No active session"));
            }

        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid session"));
        }
    }


    @GetMapping("/checkAuthentication")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        try {
            if(request.getSession(false) == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Session has expired"));
            }

            String session = request.getSession(false).getId();
            String username = request.getRemoteUser();
            // null checks
            if (username == null || session == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Session is not valid"));

            request.getSession().

            // check the database for session information and match if possible
            if (sessionService.doesSessionExist(session, username)) {
                Account account = accountService.getUserByUsername(username);
                return ResponseEntity.status(200).body(
                        Map.of("username", account.getUsername(),
                                "role", account.getRole())

                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
            }
        }
        catch(InvalidSessionException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Session"));
        }
        catch(InvalidUsernameOrPasswordException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found"));
        }
    }




}
