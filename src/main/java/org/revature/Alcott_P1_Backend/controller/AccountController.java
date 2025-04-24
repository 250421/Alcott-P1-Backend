package org.revature.Alcott_P1_Backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.revature.Alcott_P1_Backend.entity.Account;
import org.revature.Alcott_P1_Backend.exception.DuplicateUsernameException;
import org.revature.Alcott_P1_Backend.exception.InvalidUsernameOrPasswordException;
import org.revature.Alcott_P1_Backend.model.NewUserRequest;
import org.revature.Alcott_P1_Backend.service.AccountService;
import org.revature.Alcott_P1_Backend.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://localhost:5173", allowCredentials = "true")
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    SessionService sessionService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<String> registerNewUser(@RequestBody NewUserRequest newUser) throws InvalidUsernameOrPasswordException, DuplicateUsernameException {
        try {
            return ResponseEntity.ok(accountService.createNewUser(newUser).getUsername());
        }
        catch (InvalidUsernameOrPasswordException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        catch (DuplicateUsernameException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already exists");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> login(@RequestBody NewUserRequest account, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            request.getSession(true); // true = create if not exists

            return ResponseEntity.ok("Login successful");

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> logout() {
        //sessionService.endSession();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        session.setAttribute("username", username);
        model.addAttribute("user", username);
        return "dashboard";
    }




}
