package com.example.lostandfound.controller;

import com.example.lostandfound.service.FoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/claimStatus")
public class ClaimStatusController {

    @Autowired
    private FoundItemService foundItemService;

    // Handle status update when the user clicks Yes or No
    @GetMapping
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        if ("yes".equalsIgnoreCase(status)) {
            foundItemService.updateStatus(id, "CLAIMED");
            return "Thank you for confirming! Your item status has been updated to CLAIMED.";
        } else if ("no".equalsIgnoreCase(status)) {
            // If "No", show a form for the user to submit a dispute
            return """
                <html>
                    <body>
                        <p>Please provide a reason for disputing:</p>
                        <form action="/claimStatus/dispute" method="post">
                            <input type="hidden" name="id" value="%d"/>
                            <textarea name="reason" placeholder="Please explain to us what happened..." required></textarea>
                            <button type="submit">Submit</button>
                        </form>
                    </body>
                </html>
                """.formatted(id);
        }
        return "Invalid request. Please provide a valid status.";
    }

    // Handle dispute form submission
    @PostMapping("/dispute")
    public String handleDispute(@RequestParam Long id, @RequestParam String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return "Reason cannot be empty. Please provide a valid reason for disputing.";
        }

        // Update the status to disputed and store the reason
        foundItemService.updateStatusToDisputed(id, reason);

        return "Thank you for your feedback! We will look into the matter and get back to you shortly.";
    }
}
