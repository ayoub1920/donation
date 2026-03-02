package tn.esprit.donation.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "donations")

public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @NotNull
    private DonationType type;

    @Column(name = "item_name", nullable = false)
    @NotBlank
    private String itemName;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    @NotNull
    private ItemCondition condition;

    @Column(name = "anonymous")
    @Builder.Default
    private Boolean anonymous = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DonationStatus status;

    @Column(name = "donated_at")
    private LocalDateTime donatedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) {
            status = DonationStatus.PENDING;
        }
        if (donatedAt == null) {
            donatedAt = LocalDateTime.now();
        }
        if (anonymous == null) {
            anonymous = Boolean.FALSE;
        }
    }
}
