package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.controller.dto.*;
import tn.esprit.donation.services.AiService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * Analyzes a donation description and returns:
     * - improvedText: AI-enhanced description
     * - category: detected type (VETEMENT or JEU)
     * - categoryLabel: human-friendly French label
     * - impactScore: usefulness score 0-100
     * - suggestions: related items to also donate
     */
    @PostMapping("/analyze-donation")
    public ResponseEntity<AiAnalysisResponse> analyzeDonation(@RequestBody AiAnalysisRequest request) {
        try {
            AiAnalysisResponse response = aiService.analyzeDonation(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("AI analysis failed: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generates smart donation suggestions based on user's donation history.
     */
    @PostMapping("/suggestions")
    public ResponseEntity<AiSuggestionResponse> getSuggestions(@RequestBody AiSuggestionRequest request) {
        try {
            AiSuggestionResponse response = aiService.getSuggestions(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("AI suggestions failed: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
