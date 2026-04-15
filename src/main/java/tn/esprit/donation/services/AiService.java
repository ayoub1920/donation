package tn.esprit.donation.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.donation.controller.dto.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OLLAMA_URL = "https://minolingo.online/ollama/v1/completions";
    private static final String MODEL = "qwen2.5:3b";

    public AiAnalysisResponse analyzeDonation(AiAnalysisRequest request) {
        String prompt = """
                You are an AI assistant for a French donation management platform called MinoLingo.
                Analyze the donation information and respond ONLY with a valid JSON object (no markdown, no extra text) with these exact fields:
                {
                  "improvedText": "enhanced description in French (2-3 sentences, clear, compelling, and specific about the item's condition and usefulness)",
                  "category": "must be exactly VETEMENT or JEU (VETEMENT=clothes/shoes/accessories/textiles, JEU=toys/games/entertainment/puzzles/books/school supplies)",
                  "categoryLabel": "human-friendly French name (e.g. Vêtements pour enfants, Jouets éducatifs, Livres et fournitures scolaires)",
                  "impactScore": integer 0-100 (higher for: good condition, high quantity, high-need items like winter clothes or educational toys),
                  "suggestions": ["3 similar items in French the user could also donate — be specific (e.g. Manteau d'hiver taille 8 ans, Jeu de société Loto)"]
                }

                """ +
                String.format("""
                Nom de l'article : %s
                Description actuelle : %s
                Type actuel : %s
                État : %s
                Quantité : %d
                Dons précédents de l'utilisateur : %s
                """,
                        nvl(request.getItemName(), "Non précisé"),
                        nvl(request.getDescription(), "Non précisé"),
                        nvl(request.getType(), "Non précisé"),
                        nvl(request.getCondition(), "Non précisé"),
                        request.getQuantity() != null ? request.getQuantity() : 0,
                        (request.getPreviousDonationItems() != null && !request.getPreviousDonationItems().isEmpty())
                                ? String.join(", ", request.getPreviousDonationItems())
                                : "Aucun don précédent"
                );

        String rawJson = callOllama(prompt);
        return parseAnalysisResponse(rawJson);
    }

    public AiSuggestionResponse getSuggestions(AiSuggestionRequest request) {
        String previousItems = (request.getPreviousDonationItems() != null && !request.getPreviousDonationItems().isEmpty())
                ? String.join(", ", request.getPreviousDonationItems())
                : "Aucun don précédent";

        String prompt = """
                You are an AI assistant for a French donation platform MinoLingo.
                Generate personalized donation suggestions based on the user's history.
                Respond ONLY with a valid JSON object (no markdown, no extra text):
                {
                  "suggestions": ["5 specific items to donate in French — be concrete (e.g. T-shirts enfants taille 6 ans, Puzzle 100 pièces, Boîte de crayons de couleur)"],
                  "message": "one short encouraging sentence in French to motivate the user"
                }
                Prioritize items not yet donated, commonly needed by children: winter clothes, educational games, school supplies.

                Dons précédents de l'utilisateur : """ + previousItems
                + ". Suggère 5 articles utiles qu'il n'a pas encore donnés.";

        String rawJson = callOllama(prompt);
        return parseSuggestionResponse(rawJson);
    }

    private String callOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("prompt", prompt);
        body.put("max_tokens", 600);
        body.put("temperature", 0.7);
        body.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, entity, Map.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        return (String) choices.get(0).get("text");
    }

    private AiAnalysisResponse parseAnalysisResponse(String raw) {
        try {
            // Strip any markdown code fences if the model adds them
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> suggestions = new ArrayList<>();
            node.path("suggestions").forEach(n -> suggestions.add(n.asText()));
            return AiAnalysisResponse.builder()
                    .improvedText(node.path("improvedText").asText(""))
                    .category(node.path("category").asText("VETEMENT"))
                    .categoryLabel(node.path("categoryLabel").asText("Don"))
                    .impactScore(Math.min(100, Math.max(0, node.path("impactScore").asInt(50))))
                    .suggestions(suggestions)
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to parse AI analysis response: " + e.getMessage());
            return AiAnalysisResponse.builder()
                    .improvedText("")
                    .category("VETEMENT")
                    .categoryLabel("Don")
                    .impactScore(50)
                    .suggestions(List.of())
                    .build();
        }
    }

    private AiSuggestionResponse parseSuggestionResponse(String raw) {
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> suggestions = new ArrayList<>();
            node.path("suggestions").forEach(n -> suggestions.add(n.asText()));
            return AiSuggestionResponse.builder()
                    .suggestions(suggestions)
                    .message(node.path("message").asText(""))
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to parse AI suggestion response: " + e.getMessage());
            return AiSuggestionResponse.builder()
                    .suggestions(List.of("Vêtements enfants", "Jouets éducatifs", "Livres", "Fournitures scolaires", "Jeux de société"))
                    .message("Votre générosité fait une vraie différence pour les enfants !")
                    .build();
        }
    }

    // Ollama models sometimes wrap JSON in ```json ... ``` — strip it
    private String extractJson(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start != -1 && end != -1) return trimmed.substring(start, end + 1);
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start != -1 && end != -1) return trimmed.substring(start, end + 1);
        return trimmed;
    }

    private String nvl(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
