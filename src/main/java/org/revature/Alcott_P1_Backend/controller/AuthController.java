package org.revature.Alcott_P1_Backend.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        session.setAttribute("username", username);
        model.addAttribute("user", username);
        return "dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


}
