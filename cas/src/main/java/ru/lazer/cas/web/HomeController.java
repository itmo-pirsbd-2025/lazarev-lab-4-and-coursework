package ru.lazer.cas.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.lazer.cas.auth.AccessService;

@Controller
public class HomeController {
    private final AccessService access;
    public HomeController(AccessService access) { this.access = access; }

    @GetMapping("/")
    public String home(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", access.roles(auth.getName()));
        model.addAttribute("services", access.listAllowedServices(auth.getName()));
        return "home";
    }
}
