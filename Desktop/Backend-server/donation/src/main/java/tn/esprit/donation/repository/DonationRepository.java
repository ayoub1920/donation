package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.Donation;
import tn.esprit.donation.entity.DonationStatus;
import tn.esprit.donation.entity.DonationType;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, DonationStatus status);

    long countByUserIdAndType(Long userId, DonationType type);

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d WHERE d.userId = :userId")
    long sumTotalQuantityByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d WHERE d.userId = :userId AND d.status = :status")
    long sumQuantityByUserIdAndStatus(Long userId, DonationStatus status);

    @Query("SELECT FUNCTION('TO_CHAR', d.donatedAt, 'YYYY-MM') AS month, COUNT(d) AS count " +
           "FROM Donation d WHERE d.userId = :userId AND d.donatedAt IS NOT NULL " +
           "GROUP BY FUNCTION('TO_CHAR', d.donatedAt, 'YYYY-MM') " +
           "ORDER BY month DESC")
    List<Object[]> countByMonthAndUserId(Long userId);
}
