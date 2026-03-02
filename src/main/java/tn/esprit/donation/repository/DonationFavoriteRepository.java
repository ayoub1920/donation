package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.DonationFavorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationFavoriteRepository extends JpaRepository<DonationFavorite, Long> {
    List<DonationFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<DonationFavorite> findByDonationIdAndUserId(Long donationId, Long userId);
    boolean existsByDonationIdAndUserId(Long donationId, Long userId);
    void deleteByDonationIdAndUserId(Long donationId, Long userId);
}
