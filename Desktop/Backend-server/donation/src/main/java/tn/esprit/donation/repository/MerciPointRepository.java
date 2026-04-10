package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.donation.entity.MerciPoint;

import java.util.List;

public interface MerciPointRepository extends JpaRepository<MerciPoint, Long> {

    List<MerciPoint> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByDonationId(Long donationId);

    @Query("SELECT COALESCE(SUM(m.points), 0) FROM MerciPoint m WHERE m.userId = :userId")
    Integer getTotalPointsByUserId(Long userId);
}
