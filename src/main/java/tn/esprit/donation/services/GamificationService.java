package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.donation.entity.User;
import tn.esprit.donation.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User addPoints(Long userId, int points) {
        User user = getUser(userId);
        user.setPoints(user.getPoints() + points);
        user.updateLevelFromPoints();
        return userRepository.save(user);
    }

    public User grantBadge(Long userId, String badge) {
        User user = getUser(userId);
        if (!user.getBadges().contains(badge)) {
            user.getBadges().add(badge);
            return userRepository.save(user);
        }
        return user;
    }

    public GamificationView getGamification(Long userId) {
        User user = getUser(userId);
        String level = user.getLevel();
        int points = user.getPoints();
        int nextLevelPoints = User.pointsForNextLevel(level);
        int currentLevelPoints = switch (level) {
            case "Beginner" -> 0;
            case "Active" -> 101;
            case "Expert" -> 301;
            case "Ambassador" -> 601;
            default -> 0;
        };
        int pointsToNext = Math.max(0, nextLevelPoints - points);
        double progress = nextLevelPoints == Integer.MAX_VALUE ? 100.0 :
                Math.min(100.0, ((double)(points - currentLevelPoints) / (nextLevelPoints - currentLevelPoints)) * 100);

        return new GamificationView(
                points,
                level,
                user.getBadges(),
                (int)progress,
                pointsToNext,
                nextLevelPoints == Integer.MAX_VALUE ? null : nextLevelPoints
        );
    }

    public record GamificationView(
            int points,
            String level,
            List<String> badges,
            int progressPercent,
            int pointsToNextLevel,
            Integer nextLevelThreshold
    ) {}
}
