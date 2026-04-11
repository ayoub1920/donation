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
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "level", nullable = false)
    private String level;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "badge")
    @Builder.Default
    private List<String> badges = new ArrayList<>();
}
