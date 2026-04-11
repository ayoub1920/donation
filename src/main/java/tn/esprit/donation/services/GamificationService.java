package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.donation.entity.Donation;
import tn.esprit.donation.entity.DonationStatus;
import tn.esprit.donation.entity.UserGamification;
import tn.esprit.donation.repository.DonationRepository;
import tn.esprit.donation.repository.UserGamificationRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserGamificationRepository userGamificationRepository;
    private final DonationRepository donationRepository;

    private static final int POINTS_PER_DONATION_CREATED = 10;
    private static final int POINTS_BONUS_ACCEPTED = 20;

    @Transactional
    public UserGamification onDonationCreated(Donation donation) {
        UserGamification g = getOrCreate(donation.getUserId());
        g.setPoints(g.getPoints() + POINTS_PER_DONATION_CREATED);
        g.setLevel(calculateLevel(g.getPoints()));

        maybeAddBadgeFirstDonation(g, donation.getUserId());
        maybeAddBadgeAmbassador(g);

        return userGamificationRepository.save(g);
    }

    @Transactional
    public UserGamification onDonationAccepted(Donation donation) {
        UserGamification g = getOrCreate(donation.getUserId());
        g.setPoints(g.getPoints() + POINTS_BONUS_ACCEPTED);
        g.setLevel(calculateLevel(g.getPoints()));

        maybeAddBadgeFirstAccepted(g, donation.getUserId());
        maybeAddBadgeAmbassador(g);

        return userGamificationRepository.save(g);
    }

    @Transactional(readOnly = true)
    public UserGamification getGamification(Long userId) {
        return userGamificationRepository.findById(userId)
                .orElse(UserGamification.builder()
                        .userId(userId)
                        .points(0)
                        .level(calculateLevel(0))
                        .badges(new ArrayList<>())
                        .build());
    }

    private UserGamification getOrCreate(Long userId) {
        return userGamificationRepository.findById(userId)
                .orElseGet(() -> userGamificationRepository.save(UserGamification.builder()
                        .userId(userId)
                        .points(0)
                        .level(calculateLevel(0))
                        .badges(new ArrayList<>())
                        .build()));
    }

    private String calculateLevel(int points) {
        if (points <= 100) return "Beginner";
        if (points <= 300) return "Active";
        if (points <= 600) return "Expert";
        return "Ambassador";
    }

    private void addBadge(UserGamification g, String badge) {
        if (g.getBadges() == null) {
            g.setBadges(new ArrayList<>());
        }
        if (!g.getBadges().contains(badge)) {
            g.getBadges().add(badge);
        }
    }

    private void maybeAddBadgeFirstDonation(UserGamification g, Long userId) {
        long count = donationRepository.countByUserId(userId);
        if (count <= 1) {
            addBadge(g, "FIRST_DONATION");
        }
    }

    private void maybeAddBadgeFirstAccepted(UserGamification g, Long userId) {
        long accepted = donationRepository.countByUserIdAndStatus(userId, DonationStatus.ACCEPTED);
        if (accepted <= 1) {
            addBadge(g, "FIRST_ACCEPTED");
        }
    }

    private void maybeAddBadgeAmbassador(UserGamification g) {
        if ("Ambassador".equals(g.getLevel())) {
            addBadge(g, "AMBASSADOR");
        }
    }
}
