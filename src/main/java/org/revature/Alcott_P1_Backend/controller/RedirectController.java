package org.revature.Alcott_P1_Backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RedirectController {

    @GetMapping("/")
    public String home() {
        return "home"; // resolves to templates/home.html
    }

    @GetMapping("/home")
    public String homeAlias() {
        return "home";
    }

}
