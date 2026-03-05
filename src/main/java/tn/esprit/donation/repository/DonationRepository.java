package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.Donation;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d.status, COUNT(d) FROM Donation d GROUP BY d.status")
    List<Object[]> countByStatus();

    @Query("SELECT d.type, COUNT(d) FROM Donation d GROUP BY d.type")
    List<Object[]> countByType();

    @Query("SELECT FUNCTION('TO_CHAR', d.donatedAt, 'YYYY-MM'), COUNT(d) FROM Donation d WHERE d.donatedAt IS NOT NULL GROUP BY FUNCTION('TO_CHAR', d.donatedAt, 'YYYY-MM') ORDER BY FUNCTION('TO_CHAR', d.donatedAt, 'YYYY-MM') DESC")
    List<Object[]> countByMonth();

    @Query("SELECT d.condition, COUNT(d) FROM Donation d GROUP BY d.condition")
    List<Object[]> countByCondition();

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d")
    Long totalQuantity();
}
