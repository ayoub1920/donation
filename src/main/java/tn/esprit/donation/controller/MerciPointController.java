package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.entity.MerciPoint;
import tn.esprit.donation.services.MerciPointService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations/merci-points")
@RequiredArgsConstructor
public class MerciPointController {

    private final MerciPointService merciPointService;

    @GetMapping("/total/{userId}")
    public ResponseEntity<Map<String, Object>> getTotalPoints(@PathVariable Long userId) {
        int total = merciPointService.getTotalPoints(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "totalPoints", total
        ));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<MerciPoint>> getPointsHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(merciPointService.getPointsHistory(userId));
    }
}
