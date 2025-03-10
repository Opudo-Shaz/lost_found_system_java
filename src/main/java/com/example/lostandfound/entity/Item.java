package com.example.lostandfound.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class Item {
    private String name;
    private String description;
    private String category;

    @Lob
    private byte[] image;
    private String current_location;
    private Timestamp created_at;
    private LocalDate updated_at;


    // Add any common fields if necessary
}
