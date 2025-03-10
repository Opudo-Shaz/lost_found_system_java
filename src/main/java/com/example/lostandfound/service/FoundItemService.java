package com.example.lostandfound.service;

import com.example.lostandfound.controller.FoundItemController;
import com.example.lostandfound.dtos.FoundItemDTO;
import com.example.lostandfound.entity.FoundItem;
import com.example.lostandfound.enums.FoundItemStatus;
import com.example.lostandfound.exception.ItemNotFoundException;
import com.example.lostandfound.repository.FoundItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(FoundItemService.class);


    // Constructor-based injection for FoundItemRepository and UserService
    @Autowired
    public FoundItemService(FoundItemRepository foundItemRepository) {
        this.foundItemRepository = foundItemRepository;
    }

    // Get all found items
    public List<FoundItem> getAllFoundItems() {
        return foundItemRepository.findAll();
    }

    // Save or update a found item
    public void saveFoundItem(FoundItem foundItem) {
        foundItemRepository.save(foundItem);
    }

    // Get a found item by ID
    public FoundItem getFoundItemById(Long id) {
        return foundItemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Found item not found with id: " + id));
    }

    // Delete a found item by ID
    public void deleteFoundItem(Long id) {
        foundItemRepository.deleteById(id);
    }

    // Method to claim a found item
    public void claimFoundItem(Long itemId, String claimerNote, String ClaimedBy, List<String> claimerImages) {
        // Fetch the found item from the repository
        FoundItem foundItem = foundItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Found item not found"));

        // Get the current user's username from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        // Set the found item's fields
        foundItem.setStatus(FoundItemStatus.PENDING);
        foundItem.setClaimedBy(currentPrincipalName);  // Set the 'claimedBy' to the current user's username
        foundItem.setClaimerNote(claimerNote);  // Set the ownership note
        foundItem.setClaimerImages(String.valueOf(claimerImages));  // Set the claimer images

        // Save the updated found item
        foundItemRepository.save(foundItem);

        // Return the updated data as DTO
    }

    // Method to retrieve FoundItemDTO by ID (including finder email from UserService)
    public FoundItemDTO getFoundItemDTO(Long id) {

        // Get the current user's username from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        FoundItem foundItem = foundItemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Found item not found with id: " + id));


        // Get the username of the finder (assuming it is stored in the FoundItem entity)
        String finderUsername = foundItem.getItemHolderName();
        foundItem.setClaimedBy(currentPrincipalName);  // Set the 'claimedBy' to the current user's username


        //  get the email of the finder
        String finderEmail = foundItem.getFinderEmail();
        // Convert FoundItem entity to FoundItemDTO
        return new FoundItemDTO(
                foundItem.getId(),
                foundItem.getName(),
                finderEmail
        );
    }

    // Utility method to get the current user's username from the security context
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        // If the user is not authenticated, return a default or null value
        return "anonymous";
    }

    // Method to search found items based on a keyword
    public List<FoundItem> searchFoundItems(String keyword) {
        return foundItemRepository.searchByKeyword(keyword);
    }

    // Method to get the email of the user who posted a found item (using FoundItemDTO)
    public String getItemFinderEmail(Long id) {
        FoundItemDTO foundItemDTO = getFoundItemDTO(id);
        return foundItemDTO.getFinderEmail();  // This will return the email of the user
    }
    public List<FoundItem> getPendingItems() {
        List<FoundItem> items = foundItemRepository.findByStatus(FoundItemStatus.PENDING);
        items.forEach(item -> logger.info("FoundItem: {}", item));
        return items;
    }


    public void updateStatus(Long id, String status) {
        FoundItem foundItem = foundItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        foundItem.setStatus(FoundItemStatus.valueOf(status));
        foundItemRepository.save(foundItem);
    }

    public void updateStatusToDisputed(Long id, String reason) {
        FoundItem foundItem= foundItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        foundItem.setStatus(FoundItemStatus.valueOf("DISPUTED"));
        foundItem.setDisputeReason(reason);
        foundItemRepository.save(foundItem);
    }

}
