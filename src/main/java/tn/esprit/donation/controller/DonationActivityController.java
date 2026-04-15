package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.entity.DonationActivity;
import tn.esprit.donation.services.DonationActivityService;

import java.util.List;

@RestController
@RequestMapping("/api/donations/activity")
@RequiredArgsConstructor
public class DonationActivityController {

    private final DonationActivityService activityService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<DonationActivity>> getActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(activityService.getRecent(userId));
    }
}
