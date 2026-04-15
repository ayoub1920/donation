package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.donation.entity.DonationActivity;

import java.util.List;

public interface DonationActivityRepository extends JpaRepository<DonationActivity, Long> {
    List<DonationActivity> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
