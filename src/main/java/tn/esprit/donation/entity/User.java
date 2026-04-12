package tn.esprit.donation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "points", columnDefinition = "INT DEFAULT 0")
    private Integer points = 0;

    @Column(name = "level", columnDefinition = "VARCHAR(20) DEFAULT 'Beginner'")
    private String level = "Beginner";

    @ElementCollection
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "badge")
    private List<String> badges = new java.util.ArrayList<>();

    @PrePersist
    @PreUpdate
    void updateLevelFromPoints() {
        this.level = calculateLevel(this.points);
    }

    public static String calculateLevel(Integer points) {
        if (points == null || points <= 100) return "Beginner";
        if (points <= 300) return "Active";
        if (points <= 600) return "Expert";
        return "Ambassador";
    }

    public static int pointsForNextLevel(String level) {
        return switch (level) {
            case "Beginner" -> 101;
            case "Active" -> 301;
            case "Expert" -> 601;
            default -> Integer.MAX_VALUE;
        };
    }
}
