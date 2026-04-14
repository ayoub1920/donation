package tn.esprit.donation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_gamification")
public class UserGamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "points", nullable = false)
    @Builder.Default
    private int points = 0;

    @Column(name = "level", nullable = false)
    @Builder.Default
    private String level = "Beginner";

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_gamification_badges", joinColumns = @JoinColumn(name = "gamification_id"))
    @Column(name = "badge")
    @Builder.Default
    private List<String> badges = new ArrayList<>();
}
