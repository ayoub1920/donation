package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.DonationComment;

import java.util.List;

@Repository
public interface DonationCommentRepository extends JpaRepository<DonationComment, Long> {
    List<DonationComment> findByDonationIdOrderByCreatedAtDesc(Long donationId);
}
