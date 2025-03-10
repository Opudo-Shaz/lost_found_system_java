package com.example.lostandfound.entity;

import com.example.lostandfound.enums.FoundItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("FOUND")
@Table(name = "found_items")
public class FoundItem extends Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime foundDate;
    private String ItemHolderName;
    private String locationFound;
    private String finderContact;
    private String dateFound;
    private String claimedBy;
    private String finderEmail;
    private String claimerContact;
    private String claimantEmail;
    private String claimerNote;
    private String finderImages;
    private String claimerImages;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoundItemStatus status = FoundItemStatus.UNCLAIMED; // Default status is UNCLAIMED
    private String disputeReason;


}