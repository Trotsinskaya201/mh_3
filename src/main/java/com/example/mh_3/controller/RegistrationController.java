package com.example.mh_3.controller;

import com.example.mh_3.model.User;
import com.example.mh_3.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", List.of(
            User.Role.DOCTOR,
            User.Role.PATIENT
        ));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        logger.info("Attempting to register user: {}", user.getUsername());
        
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("roles", List.of(User.Role.DOCTOR, User.Role.PATIENT));
            return "register";
        }

        try {
            userService.registerUser(user);
            logger.info("User registered successfully: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            logger.error("Registration failed for user {}: {}", user.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", List.of(User.Role.DOCTOR, User.Role.PATIENT));
            return "register";
        }
    }
} 