package com.example.lostandfound.controller;

import com.example.lostandfound.entity.LostItem;
import com.example.lostandfound.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/lost-items")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    // List of categories for the dropdown
    private static final List<String> CATEGORIES = Arrays.asList(
            "Personal Items", "Clothing & Accessories", "Electronics", "Books & Stationery",
            "Outdoor & Sports Equipment", "Travel & Luggage", "Toys & Games",
            "Pets & Pet Accessories", "Food & Drink", "Miscellaneous",
            "Furniture & Household Items", "Medical Supplies", "Musical Instruments & Equipment",
            "Holiday & Seasonal Items"
    );

    // Display the list of lost items
    @GetMapping
    public String index(Model model) {
        List<LostItem> lostItems = lostItemService.getAllLostItems();
        model.addAttribute("items", lostItems);
        model.addAttribute("isRootUri", true); // Add the attribute for root URI
        return "lostItems"; // Return the specific view for lost items
    }

    @GetMapping("/post-lost-item")
    public String showPostLostItemForm(Model model) {
        model.addAttribute("lostItem", new LostItem());
        model.addAttribute("isRootUri", true);
        model.addAttribute("categories", CATEGORIES);  // Pass the categories list to the view
        return "addLostItem"; // Thymeleaf template
    }

    // Submit a new lost item (with image handling)
    @PostMapping("/save-lost-item")
    public String saveLostItem(@RequestParam("image") MultipartFile image,
                               @RequestParam("name") String name,
                               @RequestParam("description") String description,
                               @RequestParam("category") String category,
                               @RequestParam("dateLost") @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate dateLost) throws IOException {
        // Create a new LostItem instance and set its attributes from the request
        LostItem lostItem = new LostItem();
        lostItem.setName(name);
        lostItem.setDescription(description);
        lostItem.setCategory(category);
        lostItem.setDateLost(dateLost);

        // Handle the file upload for the image
        if (!image.isEmpty()) {
            lostItem.setImage(image.getBytes());  // Convert MultipartFile to byte[] and set it on the LostItem
        }

        // Save the LostItem using the service
        lostItemService.saveLostItem(lostItem);

        // Redirect to the list of lost items
        return "redirect:/lost-items";
    }

    // View a specific lost item by ID
    @GetMapping("/view-lost-item/{id}")
    public String viewLostItem(@PathVariable("id") Long id, Model model) {
        LostItem lostItem = lostItemService.getLostItemById(id);
        model.addAttribute("item", lostItem);
        model.addAttribute("isRootUri", true);
        return "viewLostItem"; // The view template to display the specific lost item
    }

    // Edit a specific lost item by ID
    @Secured("ROLE_ADMIN")  // Ensures only ADMIN users can access this page
    @GetMapping("/edit-lost-item/{id}")
    public String editLostItem(@PathVariable("id") Long id, Model model) {
        LostItem lostItem = lostItemService.getLostItemById(id);
        model.addAttribute("lostItem", lostItem);
        model.addAttribute("isRootUri", true);
        model.addAttribute("categories", CATEGORIES); // Pass the categories list for the dropdown in the edit form
        return "editLostItem"; // The view template to edit a lost item
    }

    // Save an updated lost item (handles image and other attributes)
    @PostMapping("/update-lost-item/{id}")
    @Secured("ROLE_ADMIN")  // Ensures only ADMIN users can access this page
    public String updateLostItem(@PathVariable("id") Long id,
                                 @RequestParam("image") MultipartFile image,
                                 @RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("category") String category,
                                 @RequestParam("dateLost") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateLost) throws IOException {
        LostItem existingItem = lostItemService.getLostItemById(id);

        // Update the fields of the existing item
        existingItem.setName(name);
        existingItem.setDescription(description);
        existingItem.setCategory(category);
        existingItem.setDateLost(dateLost);

        // Handle the image upload
        if (!image.isEmpty()) {
            existingItem.setImage(image.getBytes());
        }

        // Save the updated LostItem
        lostItemService.saveLostItem(existingItem);

        return "redirect:/lost-items";
    }

    // Delete a specific lost item by ID
    @Secured("ROLE_ADMIN")  // Ensures only ADMIN users can access this page
    @GetMapping("/delete-lost-item/{id}")
    public String deleteLostItem(@PathVariable("id") Long id) {
        lostItemService.deleteLostItem(id); // Delete the lost item by ID
        return "redirect:/lost-items"; // Redirect to the list of lost items after deletion
    }

    // Method to search Lost Items based on a query
    @GetMapping("/search")
    public String searchLostItems(@RequestParam("query") String query, Model model) {
        // Call the service to fetch search results
        List<LostItem> lostItems = lostItemService.searchLostItems(query);

        // Add the results to the model
        model.addAttribute("items", lostItems);
        model.addAttribute("query", query);  // Add the search query for displaying in the input field

        return "lostItems";  // Return the template for displaying results
    }
}
