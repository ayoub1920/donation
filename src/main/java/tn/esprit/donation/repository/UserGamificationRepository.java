package tn.esprit.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.donation.entity.UserGamification;

@Repository
public interface UserGamificationRepository extends JpaRepository<UserGamification, Long> {
}
