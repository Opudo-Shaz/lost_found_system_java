package com.example.lostandfound.service;

import com.example.lostandfound.LostItemNotFoundException;
import com.example.lostandfound.entity.LostItem;
import com.example.lostandfound.repository.LostItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;

    // Constructor
    public LostItemService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    // Get all lost items
    public List<LostItem> getAllLostItems() {
        return lostItemRepository.findAll();
    }

    // Save or update a lost item
    public void saveLostItem(LostItem lostItem) {
        lostItemRepository.save(lostItem);
    }

    // Get a specific lost item by ID
    public LostItem getLostItemById(Long id) {
        return lostItemRepository.findById(id)
                .orElseThrow(() -> new LostItemNotFoundException("Lost Item with ID " + id + " not found"));
    }


    // Delete a lost item by ID
    public void deleteLostItem(Long id) {
        lostItemRepository.deleteById(id);
    }


    // Method to search Lost Items based on a keyword
    public List<LostItem> searchLostItems(String keyword) {
        return lostItemRepository.searchByKeyword(keyword);
    }
}
