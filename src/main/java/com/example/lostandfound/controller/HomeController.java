package com.example.lostandfound.controller;

import com.example.lostandfound.entity.User;
import com.example.lostandfound.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserService userService;

    // Root or Home page for unauthenticated users
    @GetMapping
    public String home(Model model) {
        model.addAttribute("isRootUri", true);
        return "index"; // or your template name
    }



    // Display login page (custom login form)
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "user/login"; // Return the login view (login.html)
    }

    // Handle registration page
    @GetMapping("/register")
    public String registerPage() {
        return "user/signup"; // Return the registration form view (register.html)
    }

    // Handle the registration of a new user
    @PostMapping("/register")
    public String registerUser(User user) {
        // Encrypt the user's password and register the user
        userService.registerUser(user);
        return "redirect:/" + "user/login"; // Redirect to the login page after registration
    }

    // Handle successful login (user redirection based on role)
    @GetMapping("/home")
    public String redirectToHome(Authentication authentication) {
        if (authentication != null) {
            String role = authentication.getAuthorities().toString();
            // Check user role and redirect accordingly
            if (role.contains("ADMIN")) {
                return "redirect:/dashboard"; // Redirect to the admin dashboard
            } else if (role.contains("USER")) {
                return "redirect:index"; // Redirect to the user home page
            }
        }
        return "index"; // Default redirect if no specific role found
    }

    // Handle logout (Optional)
    @GetMapping("/logout")
    public String logoutPage(@RequestParam(value = "logout", defaultValue = "false") String logout, Model model) {
        if (logout.equals("true")) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "/user/login";
    }

    // Mapping for the Terms and Conditions page
    @GetMapping("/terms-and-conditions")
    public String showTermsAndConditions( Model model) {
        model.addAttribute("isRootUri", true);

        return "terms-and-conditions";
    }

}
