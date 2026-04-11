package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.entity.UserGamification;
import tn.esprit.donation.services.GamificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/{id}/gamification")
    public ResponseEntity<Map<String, Object>> getGamification(@PathVariable Long id) {
        UserGamification g = gamificationService.getGamification(id);
        return ResponseEntity.ok(Map.of(
                "userId", g.getUserId(),
                "points", g.getPoints(),
                "level", g.getLevel(),
                "badges", g.getBadges()
        ));
    }
}
