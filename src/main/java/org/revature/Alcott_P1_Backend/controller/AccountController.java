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
    public ResponseEntity<?> registerNewUser(@RequestBody NewUserRequest newUser) throws InvalidUsernameOrPasswordException, DuplicateUsernameException {
        try {
            accountService.createNewUser(newUser);
            return ResponseEntity.status(201).body(Map.of("message", "User created successfully"));
        }
        catch (InvalidUsernameOrPasswordException e){
            return ResponseEntity.status(400).body(Map.of("message", "Invalid username or password"));
        }
        catch (DuplicateUsernameException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists"));
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody NewUserRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = accountService.authenticateUser(request.getUsername(), request.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Create session
            HttpSession session = httpRequest.getSession(false);

            if(request.getUsername() == null){
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Username cannot be empty"));
            }
            // else if there is a pre-existing session in the database
            else if(httpRequest.getSession(false) != null && sessionService.doesSessionExist(httpRequest.getSession(false).getId(), request.getUsername())) {
                Session currentSession = sessionService.findSessionById(httpRequest.getSession(false).getId());
                // if the session has not expired yet
                if(currentSession.getExpiresAt().isAfter(LocalDateTime.now())){
                    ResponseEntity.status(401)
                            .body(Map.of("message", "User is already logged in on current session"));
                }
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

        } catch (AuthenticationException | InvalidUsernameOrPasswordException ex) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "Invalid username or password"));
        } catch (InvalidSessionException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "Invalid session"));
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
    public ResponseEntity<?> checkSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            if(request.getSession(false) == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Session has expired"));
            }

            Session session;
            String username = request.getRemoteUser();
            if(username != null && request.getSession(false) != null && sessionService.doesSessionExist(request.getSession(false).getId(), username)) {
                session = sessionService.findSessionById(request.getSession(false).getId());
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Session is not valid"));
            }

            if(session.getExpiresAt().isBefore(LocalDateTime.now())){
                sessionService.deleteBySessionId(session.getSessionId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Session has expired"));
            }
            else{
                session.setExpiresAt(LocalDateTime.now().plusMinutes(30));
                sessionService.updateSession(session);
            }

            // check the database for session information and match if possible
            Account account = accountService.getUserByUsername(username);
            Cookie cookie = request.getCookies()[0];
            cookie.setMaxAge(30 * 60);
            
            return ResponseEntity.status(200).body(
                    Map.of("username", account.getUsername(),
                            "role", account.getRole())
            );

        }
        catch(InvalidSessionException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Session"));
        }
        catch(InvalidUsernameOrPasswordException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found"));
        }
    }




}
