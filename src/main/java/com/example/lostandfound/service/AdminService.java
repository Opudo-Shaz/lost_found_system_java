package com.example.lostandfound.service;

import org.springframework.stereotype.Service;

@Service
public class AdminService {

    // Simulate a call to the database
    public long getTotalMembers() {
        // Logic to count the total members from the database
        return 1150;
    }

    public long getTotalLostItems() {
        // Logic to count the total lost items from the database
        return 150;
    }

    public long getTotalFoundItems() {
        // Logic to count the total found items from the database
        return 53;
    }
}

