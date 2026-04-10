package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.donation.entity.Donation;
import tn.esprit.donation.entity.MerciPoint;
import tn.esprit.donation.repository.MerciPointRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerciPointService {

    private final MerciPointRepository merciPointRepository;
    private final EmailService emailService;

    private static final int POINTS_PER_DONATION = 10;

    public MerciPoint awardPoints(Donation donation) {
        // Prevent double-awarding for the same donation
        if (merciPointRepository.existsByDonationId(donation.getId())) {
            throw new IllegalStateException("Points already awarded for this donation");
        }

        int points = calculatePoints(donation);

        MerciPoint merciPoint = MerciPoint.builder()
                .userId(donation.getUserId())
                .donation(donation)
                .points(points)
                .reason("Don effectué : " + donation.getItemName() + " (x" + donation.getQuantity() + ")")
                .build();

        MerciPoint saved = merciPointRepository.save(merciPoint);

        // Send email notification about points earned
        try {
            emailService.sendMerciPointsEmail(donation, points, getTotalPoints(donation.getUserId()));
        } catch (Exception e) {
            System.err.println("Failed to send merci points email: " + e.getMessage());
        }

        return saved;
    }

    public int getTotalPoints(Long userId) {
        return merciPointRepository.getTotalPointsByUserId(userId);
    }

    public List<MerciPoint> getPointsHistory(Long userId) {
        return merciPointRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private int calculatePoints(Donation donation) {
        int base = POINTS_PER_DONATION;
        // Bonus points based on quantity
        if (donation.getQuantity() != null && donation.getQuantity() > 1) {
            base += donation.getQuantity() * 2;
        }
        return base;
    }
}
