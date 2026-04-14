package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.donation.entity.UserGamification;

import java.util.Optional;

public interface UserGamificationRepository extends JpaRepository<UserGamification, Long> {
    Optional<UserGamification> findByUserId(Long userId);
}
