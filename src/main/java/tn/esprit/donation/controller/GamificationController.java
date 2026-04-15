package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.services.GamificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/{id}/gamification")
    public ResponseEntity<Map<String, Object>> getGamification(@PathVariable Long id) {
        return ResponseEntity.ok(gamificationService.getGamificationResponse(id));
    }
}
