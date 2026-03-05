package tn.esprit.donation.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUserEmail(Long userId) {
        try {
            // TODO: Replace with actual user service call
            // For now, return a mock email based on userId
            if (userId == null) {
                return "anonymous@minolingo.com";
            }
            
            // Mock implementation - replace with actual user service
            String[] mockEmails = {
                "user1@example.com",
                "user2@example.com", 
                "user3@example.com",
                "test@minolingo.com"
            };
            
            int index = (int)(userId % mockEmails.length);
            return mockEmails[index];
        } catch (Exception e) {
            System.err.println("Error getting user email: " + e.getMessage());
            return "user@example.com";
        }
    }
}
