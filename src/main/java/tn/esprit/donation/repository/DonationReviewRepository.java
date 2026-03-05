package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.DonationReview;

import java.util.List;

@Repository
public interface DonationReviewRepository extends JpaRepository<DonationReview, Long> {
    List<DonationReview> findByDonationIdOrderByCreatedAtDesc(Long donationId);
}
