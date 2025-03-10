package com.example.lostandfound.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FoundItemDTO {
    private Long id;
    private String name;
    private String claimedBy;
    private String finderEmail;
    private String finderContact;

    // Constructor
    public FoundItemDTO(Long id, String name, String claimedBy) {
        this.id = id;
        this.name = name;
        this.claimedBy = claimedBy;

    }
}
