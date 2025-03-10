package com.example.lostandfound.controller;

import com.example.lostandfound.dtos.ClaimFoundItemDTO;
import com.example.lostandfound.dtos.FoundItemDTO;
import com.example.lostandfound.entity.FoundItem;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.service.UserService;
import com.example.lostandfound.service.EmailService;
import com.example.lostandfound.service.FileUploadService;
import com.example.lostandfound.service.FoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/found-items")
public class FoundItemController {
    private static final Logger logger = LoggerFactory.getLogger(FoundItemController.class);


    private final FoundItemService foundItemService;
    private final FileUploadService fileUploadService;
    private final EmailService emailService;
    private final UserService userService;


    // Constructor-based injection for required services
    @Autowired
    public FoundItemController(FoundItemService foundItemService, FileUploadService fileUploadService, EmailService emailService, UserService userService) {
        this.foundItemService = foundItemService;
        this.fileUploadService = fileUploadService;
        this.emailService = emailService;
        this.userService = userService;
    }

    // List of categories for the dropdown
    private static final List<String> CATEGORIES = Arrays.asList(
            "Personal Items", "Clothing & Accessories", "Electronics", "Books & Stationery",
            "Outdoor & Sports Equipment", "Travel & Luggage", "Toys & Games",
            "Pets & Pet Accessories", "Food & Drink", "Miscellaneous",
            "Furniture & Household Items", "Medical Supplies", "Musical Instruments & Equipment",
            "Holiday & Seasonal Items"
    );

    // Display the list of found items
    @GetMapping
    public String index(Model model) {
        logger.info("Fetching all found items.");
        List<FoundItem> foundItems = foundItemService.getAllFoundItems();
        model.addAttribute("items", foundItems);
        model.addAttribute("isRootUri", true);
        return "foundItems";
    }

    // Show form to post a new-found item
    @GetMapping("/post-found-item")
    public String showPostFoundItemForm(Model model) {
        logger.info("Displaying form to post a new found item.");
        model.addAttribute("foundItem", new FoundItem());
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("isRootUri", true);
        return "addFoundItem"; // Thymeleaf template for posting new-found item
    }

    // Submit a new-found item
    @PostMapping("/post-found-item")
    public String postFoundItem(@ModelAttribute FoundItem foundItem, RedirectAttributes redirectAttributes) {
        logger.info("Posting a new found item: {}", foundItem);
        try {
            foundItemService.saveFoundItem(foundItem);
            redirectAttributes.addFlashAttribute("success", "Found item posted successfully!");
        } catch (Exception e) {
            logger.error("Error posting found item.", e);
            redirectAttributes.addFlashAttribute("error", "Error posting found item.");
        }
        return "redirect:/found-items";
    }

    // View a specific found item by ID
    @GetMapping("/view-found-item/{id}")
    public String viewFoundItem(@PathVariable("id") Long id, Model model) {
        logger.info("Viewing found item with ID: {}", id);
        try {
            FoundItem foundItem = foundItemService.getFoundItemById(id);
            model.addAttribute("foundItem", foundItem);
            model.addAttribute("isRootUri", true);
            return "viewFoundItem"; // Display the specific found item
        } catch (Exception e) {
            logger.error("Found item with ID {} not found.", id, e);
            model.addAttribute("error", "Found item not found.");
            return "error"; // Show error page if not found
        }
    }

    // Edit a specific found item by ID (Admin-only access)
    @Secured("ROLE_ADMIN")
    @GetMapping("/edit-found-item/{id}")
    public String editFoundItem(@PathVariable("id") Long id, Model model) {
        logger.info("Editing found item with ID: {}", id);
        try {
            FoundItem foundItem = foundItemService.getFoundItemById(id);
            model.addAttribute("foundItem", foundItem);
            model.addAttribute("categories", CATEGORIES);
            model.addAttribute("isRootUri", true);
            return "editFoundItem"; // Edit found item page
        } catch (Exception e) {
            logger.error("Error retrieving found item with ID {} for editing.", id, e);
            model.addAttribute("error", "Found item not found.");
            return "error";
        }
    }

    // Save the edited found item
    @PostMapping("/save-found-item")
    public String saveFoundItem(@ModelAttribute FoundItem foundItem, RedirectAttributes redirectAttributes) {
        logger.info("Saving found item: {}", foundItem);
        try {
            foundItemService.saveFoundItem(foundItem);
            redirectAttributes.addFlashAttribute("success", "Found item updated successfully!");
            logger.info("Found item updated successfully.");
        } catch (Exception e) {
            logger.error("Error saving found item.", e);
            redirectAttributes.addFlashAttribute("error", "Error saving found item.");
        }
        return "redirect:/found-items";
    }

    // Delete a specific found item by ID (Admin-only access)
    @Secured("ROLE_ADMIN")
    @GetMapping("/delete-found-item/{id}")
    public String deleteFoundItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            foundItemService.deleteFoundItem(id);
            redirectAttributes.addFlashAttribute("success", "Found item deleted successfully!");
            logger.info("Found item deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting found item with ID {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "Error deleting found item.");
        }
        return "redirect:/found-items";
    }

    // Show the form for claiming a found item
    @GetMapping("/claim/{id}")
    public String showClaimForm(@PathVariable("id") Long id, Model model) {
        logger.info("Showing claim form for found item ID: {}", id);
        ClaimFoundItemDTO claim = new ClaimFoundItemDTO();
        claim.setItemId(id);
        model.addAttribute("claim", claim);
        model.addAttribute("isRootUri", true);
        return "claimFoundItem"; // Claim form template
    }

    @PostMapping("/save-claim/{id}")
    public String saveClaim(@ModelAttribute("claim") ClaimFoundItemDTO claimDTO,
                            @RequestParam("image") MultipartFile image,
                            @RequestParam("receipt") MultipartFile receipt,
                            RedirectAttributes redirectAttributes,
                            @PathVariable Long id) {
        logger.info("Submitting claim for found item ID: {}", id);

        try {
            // Retrieve the FoundItemDTO by its ID
            FoundItemDTO foundItemDTO = foundItemService.getFoundItemDTO(id);
            String finderEmail = foundItemDTO.getFinderEmail();
            String finderContact = foundItemDTO.getFinderContact();

            if (finderEmail == null || finderEmail.isEmpty()) {
                throw new IllegalArgumentException("Finder's email is missing or invalid.");
            }

            // Handle file uploads for image and receipt
            String imagePath = fileUploadService.uploadFile(image); // Upload image
            String receiptPath = fileUploadService.uploadFile(receipt); // Upload receipt

            // Prepare the claimer images list
            List<String> claimerImages = new ArrayList<>();
            claimerImages.add(imagePath);
            claimerImages.add(receiptPath); // Add receipt path to the images list

            // Get the current user's details
            User currentUser = userService.getCurrentUser();

            String claimantEmail = currentUser.getEmail(); // Email of the claimant
            String claimantContact = currentUser.getContact(); // Contact of the claimant

            // Update the found item
            foundItemService.claimFoundItem(
                    id,
                    claimDTO.getClaimerNote(),
                    currentUser.getUsername(), // Authenticated user as claimant
                    claimerImages
            );

            // Send email notification to the finder with claim details
            emailService.sendClaimNotificationEmail(
                    finderEmail, // Finder's email
                    currentUser.getUsername(), // Claimer's username
                    foundItemDTO.getName(), // Item name
                    claimantContact, // Claimer's contact
                    claimDTO.getClaimerNote(), // Note from the claimer
                    String.join(", ", claimerImages) // Comma-separated list of claimer images
            );

            // Send email notification to the claimant with finder details
            emailService.sendClaimantNotificationEmail(
                    claimantEmail,
                    foundItemDTO.getName(),
                    finderContact, // Finder's contact details
                    finderEmail, // Finder's email
                    claimDTO.getLocationFound() // Location where the item was found
            );

            // Prepare a success message
            String successMessage = String.format(
                    "Claim submitted successfully! You can now contact the finder to reunite with your item. " +
                            "The item '%s' was found by a user reachable at Contact: %s, Email: %s, and the location where it was found is %s.",
                    foundItemDTO.getName(),
                    finderContact,
                    finderEmail,
                    claimDTO.getLocationFound()
            );

            // Add a success message and redirect
            logger.info("Claim submitted successfully for item ID: {}", id);
            redirectAttributes.addFlashAttribute("success", successMessage);

        } catch (Exception e) {
            logger.error("Error submitting claim for item ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error submitting claim.");
        }
        return "redirect:/found-items";
    }



    // Method to search Found Items based on a query
    @GetMapping("/search")
    public String searchFoundItems(@RequestParam("query") String query, Model model) {
        List<FoundItem> foundItems = foundItemService.searchFoundItems(query);
        model.addAttribute("items", foundItems);
        model.addAttribute("query", query);
        logger.info("Search results for query '{}': {} items found.", query, foundItems.size());
        return "foundItems";
    }
}
