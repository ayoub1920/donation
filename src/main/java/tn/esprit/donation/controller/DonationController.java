package tn.esprit.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tn.esprit.donation.entity.Donation;
import tn.esprit.donation.entity.DonationStatus;
import tn.esprit.donation.services.DonationService;
import tn.esprit.donation.services.DonationStatsService;
import tn.esprit.donation.entity.DonationReview;
import tn.esprit.donation.entity.DonationFavorite;
import tn.esprit.donation.entity.DonationComment;
import tn.esprit.donation.controller.dto.CommentRequest;
import tn.esprit.donation.controller.dto.ReviewRequest;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DonationStatsService donationStatsService;

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getStats() {
        return ResponseEntity.ok(donationStatsService.getStats());
    }

    @PostMapping("/create-donation")
    public ResponseEntity<Donation> create(@Valid @RequestBody Donation donation) {
        Donation created = donationService.create(donation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/get-all-donations")   
    public ResponseEntity<List<Donation>> getAll() {
        return ResponseEntity.ok(donationService.getAll());
    }

    @GetMapping("/get-donation/{id}")
    public ResponseEntity<Donation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getById(id));
    }

    @PutMapping("/update-donation/{id}")
    public ResponseEntity<Donation> update(@PathVariable Long id,
                                           @Valid @RequestBody Donation donation) {
        return ResponseEntity.ok(donationService.update(id, donation));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String filename = LocalDateTime.now().toString().replace(":", "-") + "-" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        String url = "/uploads/" + filename;
        return ResponseEntity.ok(url);
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<Donation> updateStatus(@PathVariable Long id,
                                                 @RequestParam("status") DonationStatus status) {
        return ResponseEntity.ok(donationService.updateStatus(id, status));
    }

    @DeleteMapping("/delete-donation/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest req) {
        try {
            DonationReview created = donationService.reviewDonation(id, req.getModeratorId(), req.getDecision(), req.getReason());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<DonationReview>> getReviews(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getReviews(id));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<DonationFavorite> addFavorite(@PathVariable Long id, @RequestParam("userId") Long userId) {
        DonationFavorite fav = donationService.addFavorite(id, userId);
        return new ResponseEntity<>(fav, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id, @RequestParam("userId") Long userId) {
        donationService.removeFavorite(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Donation>> getFavorites(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(donationService.getFavorites(userId));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest req) {
        try {
            DonationComment created = donationService.addComment(id, req.getUserId(), req.getText());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<DonationComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getComments(id));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @PathVariable Long commentId) {
        donationService.deleteComment(id, commentId);
        return ResponseEntity.noContent().build();
    }
}
