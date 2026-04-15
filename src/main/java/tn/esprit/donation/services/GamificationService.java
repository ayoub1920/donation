package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.donation.entity.DonationActivityType;
import tn.esprit.donation.entity.UserGamification;
import tn.esprit.donation.repository.DonationRepository;
import tn.esprit.donation.repository.UserGamificationRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserGamificationRepository gamificationRepository;
    private final DonationRepository donationRepository;
    private final DonationActivityService activityService;

    // Level thresholds
    private static final int ACTIVE_MIN    = 101;
    private static final int EXPERT_MIN    = 301;
    private static final int AMBASSADOR_MIN = 600;

    // Badge names
    public static final String BADGE_FIRST_DONATION   = "Premier Don";
    public static final String BADGE_ACTIVE_DONOR     = "Donateur Actif";
    public static final String BADGE_HERO             = "Héros du Don";
    public static final String BADGE_ACCEPTED         = "Don Accepté";
    public static final String BADGE_AMBASSADOR       = "Ambassadeur";

    @Transactional
    public UserGamification addPoints(Long userId, int points, String reason) {
        UserGamification gami = getOrCreate(userId);
        String oldLevel = gami.getLevel();
        gami.setPoints(gami.getPoints() + points);
        gami.setLevel(calculateLevel(gami.getPoints()));
        checkAndAwardBadges(gami);
        UserGamification saved = gamificationRepository.save(gami);

        // Log points earned
        activityService.log(userId, DonationActivityType.POINTS_GAGNES,
                "+" + points + " points — " + reason, null, points);

        // Log level change if upgraded
        if (!gami.getLevel().equals(oldLevel)) {
            activityService.log(userId, DonationActivityType.NIVEAU_ATTEINT,
                    "Nouveau niveau atteint : " + gami.getLevel(), null, 0);
        }

        return saved;
    }

    public UserGamification getGamification(Long userId) {
        return getOrCreate(userId);
    }

    @Transactional
    public Map<String, Object> getGamificationResponse(Long userId) {
        UserGamification gami = getGamification(userId);
        int next = nextLevelThreshold(gami.getPoints());
        int prev = prevLevelThreshold(gami.getPoints());
        int range = next - prev;
        int progress = range > 0 ? (int) Math.round((gami.getPoints() - prev) * 100.0 / range) : 100;

        return Map.of(
                "userId",        userId,
                "points",        gami.getPoints(),
                "level",         gami.getLevel(),
                "badges",        gami.getBadges(),
                "nextLevel",     nextLevelName(gami.getPoints()),
                "pointsToNext",  Math.max(0, next - gami.getPoints()),
                "progress",      Math.min(100, progress)
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private UserGamification getOrCreate(Long userId) {
        return gamificationRepository.findByUserId(userId).orElseGet(() -> {
            UserGamification g = UserGamification.builder().userId(userId).build();
            return gamificationRepository.save(g);
        });
    }

    public String calculateLevel(int points) {
        if (points >= AMBASSADOR_MIN) return "Ambassador";
        if (points >= EXPERT_MIN)     return "Expert";
        if (points >= ACTIVE_MIN)     return "Active";
        return "Beginner";
    }

    private void checkAndAwardBadges(UserGamification gami) {
        Long userId = gami.getUserId();
        long totalDonations = donationRepository.countByUserId(userId);

        awardIf(gami, BADGE_FIRST_DONATION, totalDonations >= 1);
        awardIf(gami, BADGE_ACTIVE_DONOR,   totalDonations >= 5);
        awardIf(gami, BADGE_HERO,           totalDonations >= 10);
        awardIf(gami, BADGE_AMBASSADOR,     "Ambassador".equals(gami.getLevel()));
    }

    @Transactional
    public void awardAcceptedBadge(Long userId) {
        UserGamification gami = getOrCreate(userId);
        awardIf(gami, BADGE_ACCEPTED, true);
        gamificationRepository.save(gami);
    }

    private void awardIf(UserGamification gami, String badge, boolean condition) {
        if (condition && !gami.getBadges().contains(badge)) {
            gami.getBadges().add(badge);
            activityService.log(gami.getUserId(), DonationActivityType.BADGE_DEBLOQUE,
                    "Badge débloqué : " + badge, null, 0);
        }
    }

    private int nextLevelThreshold(int points) {
        if (points < ACTIVE_MIN)     return ACTIVE_MIN;
        if (points < EXPERT_MIN)     return EXPERT_MIN;
        if (points < AMBASSADOR_MIN) return AMBASSADOR_MIN;
        return AMBASSADOR_MIN; // already at max level
    }

    private int prevLevelThreshold(int points) {
        if (points >= AMBASSADOR_MIN) return AMBASSADOR_MIN;
        if (points >= EXPERT_MIN)     return EXPERT_MIN;
        if (points >= ACTIVE_MIN)     return ACTIVE_MIN;
        return 0;
    }

    private String nextLevelName(int points) {
        if (points < ACTIVE_MIN)     return "Active";
        if (points < EXPERT_MIN)     return "Expert";
        if (points < AMBASSADOR_MIN) return "Ambassador";
        return "Ambassador"; // max level
    }
}
