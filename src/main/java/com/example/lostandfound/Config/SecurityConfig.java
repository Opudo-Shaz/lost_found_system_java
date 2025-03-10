package com.example.lostandfound.Config;

import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserDetailsService userDetailsService;

    // Define the security filter chain for HTTP security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/register", "/login", "/", "/lost-items","/terms-and-conditions", "/password/**","/found-items", "/found-items/view-found-item/{id}","/found-items/claim", "/lost-items/view-lost-item/{id}","/lost-items/search", "/found-items/search","/css/**","/vendor/**","/js/**", "/img/**").permitAll()  // Public pages
                                .requestMatchers("/dashboard", "/lost-items/edit-lost-item/{id}","/found-items/edit-found-item/{id}").hasRole("ADMIN")  // Admin-only pages
                                .requestMatchers("/index", "/post-lost-item", "/post-found-item", "/found-items/claim").hasRole("USER")   // User-only pages
                                .anyRequest().authenticated()  // Any other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")  // Redirects to login page with error query parameter if login fails
                        .defaultSuccessUrl("/")
                        .permitAll()  // Allow everyone to access the login page
                )
                .logout(LogoutConfigurer::permitAll
                );

        return http.build();  // Return the HTTP security configuration
    }

    // Define the authentication manager to manage authentication logic
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)  // Custom UserDetailsService to load users
                .passwordEncoder(passwordEncoder())  // Password encoder for encoding passwords
                .and()
                .build();
    }


    // Define the password encoder bean to encrypt passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Using BCrypt for password encoding
    }
}
