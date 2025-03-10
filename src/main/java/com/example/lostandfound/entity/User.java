package com.example.lostandfound.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.PropertyValues;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String Contact;
    private String address;
    private String role = "USER";
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String avatarImage;


    // Set a default role if it's not provided
    public String getRole() {
        if (role == null || role.isEmpty()) {
            return "USER";  // Default role
        }
        return role.toUpperCase();  // Ensure the role is in uppercase
    }



}
