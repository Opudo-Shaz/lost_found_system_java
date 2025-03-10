package com.example.lostandfound.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String recipientEmail, String subject, String body, boolean isHtml) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            throw new IllegalArgumentException("Recipient email is missing or invalid.");
        }

        try {
            // Create a MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set email details
            helper.setFrom("patikalostandfound@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);

            // Pass `isHtml` to determine if the body should be treated as HTML
            helper.setText(body, isHtml);

            // Send the email
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendClaimNotificationEmail(
            String finderEmail,
            String claimedBy,
            String itemName,
            String claimerContact,
            String claimerNote,
            String claimerImages) {

        String subject = "Claim Placed for Your Found Item";

        String body = String.format("""
                <html>
                <body>
                    <p>Hello,</p>
                    <p>A claim has been placed for your found item: <strong>'%s'</strong> by <strong>%s</strong>.</p>
                    <p><strong>Claimer's Contact:</strong> %s</p>
                    <p><strong>Claimer's Note:</strong> %s</p>
                    <p>Please review the claim and take the necessary actions. Below are the details to help you verify the claim:</p>
                    <p><strong>Claimer's Images:</strong></p>
                    <p><img src="%s" alt="Claimer's Image" style="max-width: 100%%; height: auto;"/></p>
                    <p>Best regards,</p>
                    <p>Patika Lost & Found Team</p>
                </body>
                </html>
                """,
                itemName,
                claimedBy,
                claimerContact,
                claimerNote,
                claimerImages);

        sendEmail(finderEmail, subject, body, true);
    }



    public void sendClaimantNotificationEmail(
            String claimantEmail,
            String itemName,
            String finderContact,
            String locationFound, String found) {

        String subject = "Your Claim Submission Details";

        String body = String.format("""
            <html>
            <body>
                <p>Hello,</p>
                <p>Thank you for submitting a claim for the item: <strong>'%s'</strong>.</p>
                <p>The finder has been notified and will review your claim. Here are the details of the finder to help you follow up:</p>
                <p><strong>Finder's Contact:</strong> %s</p>
                <p><strong>Found Item Location:</strong> %s</p>
                <p>Please wait for the finder to contact you or feel free to reach out to them directly if needed.</p>
                <p>Best regards,</p>
                <p>Patika Lost & Found Team</p>
            </body>
            </html>
            """, itemName, finderContact, locationFound);

        sendEmail(claimantEmail, subject, body, true);
    }

    // Method for follow-up notification
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendFollowUpNotification(String claimantEmail, String itemName, Long itemId) {
        String subject = "Have you retrieved your item?";

        String body = String.format("""
            <html>
            <body>
                <p>Dear User,</p>
                <p>We noticed that the item you claimed (<strong>%s</strong>) is still marked as pending in our system.</p>
                <p>Please let us know if you have retrieved it by clicking one of the buttons below:</p>
                <p>
                    <a href="%s/claimStatus?id=%d&status=yes" style="padding: 10px 20px; color: white; background-color: green; text-decoration: none; border-radius: 5px;">Yes</a>
                    <a href="%s/claimStatus?id=%d&status=no" style="padding: 10px 20px; color: white; background-color: red; text-decoration: none; border-radius: 5px;">No</a>
                </p>
                <p>Thank you,</p>
                <p>Patika Lost and Found System Team</p>
            </body>
            </html>
            """, itemName, baseUrl, itemId, baseUrl, itemId);

        sendEmail(claimantEmail, subject, body, true);
    }


}
