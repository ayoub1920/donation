package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.donation.entity.DonationActivity;
import tn.esprit.donation.entity.DonationActivityType;
import tn.esprit.donation.repository.DonationActivityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationActivityService {

    private final DonationActivityRepository repository;

    public void log(Long userId, DonationActivityType type, String message, Long donationId, int points) {
        DonationActivity activity = DonationActivity.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .donationId(donationId)
                .points(points)
                .build();
        repository.save(activity);
    }

    public List<DonationActivity> getRecent(Long userId) {
        return repository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
    }
}
