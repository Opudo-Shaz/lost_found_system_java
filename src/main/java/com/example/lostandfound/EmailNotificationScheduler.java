package com.example.lostandfound;
import com.example.lostandfound.entity.FoundItem;
import com.example.lostandfound.service.EmailService;
import com.example.lostandfound.service.FoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailNotificationScheduler {

    private final EmailService emailService;
    private final FoundItemService foundItemService;

    @Autowired
    public EmailNotificationScheduler(EmailService emailService, FoundItemService foundItemService) {
        this.emailService = emailService;
        this.foundItemService = foundItemService;
    }

    // Schedule the task to run every two days
    @Scheduled(fixedRate = 172800000) // 2 days in milliseconds
    public void sendPendingItemNotifications() {
        List<FoundItem> pendingItems = foundItemService.getPendingItems(); // Fetch items with "PENDING" status

        for (FoundItem foundItem : pendingItems) {
            // Ensure that the email, item name, and ID are not null
            if (foundItem.getClaimantEmail() != null && !foundItem.getClaimantEmail().isEmpty()
                    && foundItem.getName() != null
                    && foundItem.getId() != null) {
                // Call the email service method
                emailService.sendFollowUpNotification(
                        foundItem.getClaimantEmail(),
                        foundItem.getName(),
                        foundItem.getId()
                );
            } else {
                // Log a warning or handle invalid data
                System.err.println("Skipping item due to missing data: " + foundItem);
            }
        }
    }
}


