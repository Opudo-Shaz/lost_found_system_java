package com.example.lostandfound.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ClaimFoundItemDTO {
    private Long itemId;
    private String itemName;
    private String claimedBy;
    private String claimerContact;
    private String claimantEmail;
    private String locationFound;
    private String claimerNote;
    private List<String> claimerImages;
    private String status;

    // Constructor
    public ClaimFoundItemDTO() {}

    public ClaimFoundItemDTO(Long itemId,String itemName, String claimedBy,String status,String claimerContact, String claimantEmail , String locationFound ,String claimerNote, List<String> claimerImages) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.claimedBy = claimedBy;
        this.claimerNote = claimerNote;
        this.claimerImages = claimerImages;
        this.claimerContact = claimerContact;
        this.claimantEmail = claimantEmail;
        this.locationFound = locationFound;
        this.status = status;
    }
}

