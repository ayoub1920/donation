package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.entity.UserGamification;
import tn.esprit.donation.services.GamificationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/{id}/gamification")
    public ResponseEntity<Map<String, Object>> getGamification(@PathVariable Long id) {
        UserGamification gamification = gamificationService.getGamification(id);
        int progress = gamificationService.getProgressToNextLevel(gamification);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", gamification.getUserId());
        response.put("points", gamification.getTotalPoints());
        response.put("level", gamification.getLevel());
        response.put("badges", gamification.getBadges());
        response.put("progress", progress);
        response.put("nextLevel", getNextLevel(gamification.getLevel()));
        response.put("pointsToNext", getPointsToNext(gamification.getTotalPoints(), gamification.getLevel()));

        return ResponseEntity.ok(response);
    }

    private String getNextLevel(String currentLevel) {
        return switch (currentLevel) {
            case "Beginner" -> "Active";
            case "Active" -> "Expert";
            case "Expert" -> "Ambassador";
            case "Ambassador" -> "Ambassador";
            default -> "Active";
        };
    }

    private int getPointsToNext(int currentPoints, String currentLevel) {
        return switch (currentLevel) {
            case "Beginner" -> Math.max(0, 100 - currentPoints);
            case "Active" -> Math.max(0, 300 - currentPoints);
            case "Expert" -> Math.max(0, 600 - currentPoints);
            case "Ambassador" -> 0;
            default -> Math.max(0, 100 - currentPoints);
        };
    }
}
