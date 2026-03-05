package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.donation.entity.Donation;
import tn.esprit.donation.entity.DonationStatus;
import tn.esprit.donation.entity.DonationReview;
import tn.esprit.donation.entity.DonationFavorite;
import tn.esprit.donation.entity.DonationComment;
import tn.esprit.donation.repository.DonationRepository;
import tn.esprit.donation.repository.DonationReviewRepository;
import tn.esprit.donation.repository.DonationFavoriteRepository;
import tn.esprit.donation.repository.DonationCommentRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tn.esprit.donation.entity.DonationType;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationReviewRepository donationReviewRepository;
    private final DonationFavoriteRepository donationFavoriteRepository;
    private final DonationCommentRepository donationCommentRepository;

    private final EmailService emailService;
    private final MerciPointService merciPointService;

    public Donation create(Donation donation) {
        Donation savedDonation = donationRepository.save(donation);
        System.out.println("[DEBUG] Donation created: id=" + savedDonation.getId() + ", userId=" + savedDonation.getUserId());
        
        // Award MERCI points on donation creation
        try {
            merciPointService.awardPoints(savedDonation);
            System.out.println("[DEBUG] MERCI points awarded successfully");
        } catch (Exception e) {
            System.err.println("Failed to award merci points: " + e.getMessage());
            e.printStackTrace();
        }

        // Send thank you email
        try {
            System.out.println("[DEBUG] Sending thank you email...");
            emailService.sendDonationThankYouEmail(savedDonation);
        } catch (Exception e) {
            System.err.println("Failed to send thank you email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return savedDonation;
    }

    public Donation update(Long id, Donation donation) {
        Donation existing = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));

        existing.setUserId(donation.getUserId());
        existing.setType(donation.getType());
        existing.setItemName(donation.getItemName());
        existing.setDescription(donation.getDescription());
        existing.setQuantity(donation.getQuantity());
        existing.setCondition(donation.getCondition());
        existing.setAnonymous(donation.getAnonymous());
        existing.setImageUrl(donation.getImageUrl());

        return donationRepository.save(existing);
    }

    public Donation updateStatus(Long id, DonationStatus status) {
        Donation existing = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));
        existing.setStatus(status);
        return donationRepository.save(existing);
    }

    public DonationReview reviewDonation(Long id, Long moderatorId, DonationStatus decision, String reason) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));

        if (decision == DonationStatus.REJECTED) {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("Reason is required when rejecting a donation");
            }
        }

        if (decision == DonationStatus.ACCEPTED) {
            if (donation.getQuantity() == null || donation.getQuantity() <= 0) {
                throw new IllegalStateException("Cannot accept a donation with invalid quantity");
            }
        }

        donation.setStatus(decision);
        donationRepository.save(donation);

        DonationReview review = DonationReview.builder()
                .donation(donation)
                .moderatorId(moderatorId)
                .decision(decision)
                .reason(reason)
                .build();
        return donationReviewRepository.save(review);
    }

    public List<DonationReview> getReviews(Long id) {
        return donationReviewRepository.findByDonationIdOrderByCreatedAtDesc(id);
    }

    public DonationFavorite addFavorite(Long donationId, Long userId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + donationId));
        if (donationFavoriteRepository.existsByDonationIdAndUserId(donationId, userId)) {
            return donationFavoriteRepository.findByDonationIdAndUserId(donationId, userId).get();
        }
        DonationFavorite fav = DonationFavorite.builder()
                .donation(donation)
                .userId(userId)
                .build();
        return donationFavoriteRepository.save(fav);
    }

    public void removeFavorite(Long donationId, Long userId) {
        if (!donationFavoriteRepository.existsByDonationIdAndUserId(donationId, userId)) {
            throw new RuntimeException("Favorite not found");
        }
        donationFavoriteRepository.deleteByDonationIdAndUserId(donationId, userId);
    }

    public List<Donation> getFavorites(Long userId) {
        return donationFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(DonationFavorite::getDonation)
                .collect(Collectors.toList());
    }

    public DonationComment addComment(Long donationId, Long userId, String text) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + donationId));
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text is required");
        }
        DonationComment c = DonationComment.builder()
                .donation(donation)
                .userId(userId)
                .text(text)
                .build();
        return donationCommentRepository.save(c);
    }

    public List<DonationComment> getComments(Long donationId) {
        return donationCommentRepository.findByDonationIdOrderByCreatedAtDesc(donationId);
    }

    public void deleteComment(Long donationId, Long commentId) {
        DonationComment c = donationCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!c.getDonation().getId().equals(donationId)) {
            throw new RuntimeException("Comment does not belong to the donation");
        }
        donationCommentRepository.deleteById(commentId);
    }

    public void delete(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new RuntimeException("Donation not found with id: " + id);
        }
        donationRepository.deleteById(id);
    }

    public Donation getById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));
    }

    public List<Donation> getAll() {
        return donationRepository.findAll();
    }

    public Map<String, Object> getStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // Total
        stats.put("totalDonations", donationRepository.countByUserId(userId));
        stats.put("totalItems", donationRepository.sumTotalQuantityByUserId(userId));

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        for (DonationStatus s : DonationStatus.values()) {
            byStatus.put(s.name(), donationRepository.countByUserIdAndStatus(userId, s));
        }
        stats.put("byStatus", byStatus);

        // By type
        Map<String, Long> byType = new HashMap<>();
        for (DonationType t : DonationType.values()) {
            byType.put(t.name(), donationRepository.countByUserIdAndType(userId, t));
        }
        stats.put("byType", byType);

        // Items by status
        Map<String, Long> itemsByStatus = new HashMap<>();
        for (DonationStatus s : DonationStatus.values()) {
            itemsByStatus.put(s.name(), donationRepository.sumQuantityByUserIdAndStatus(userId, s));
        }
        stats.put("itemsByStatus", itemsByStatus);

        // By month
        Map<String, Long> byMonth = new LinkedHashMap<>();
        for (Object[] row : donationRepository.countByMonthAndUserId(userId)) {
            byMonth.put((String) row[0], (Long) row[1]);
        }
        stats.put("byMonth", byMonth);

        return stats;
    }
}
