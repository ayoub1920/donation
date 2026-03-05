package tn.esprit.donation.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.donation.entity.DonationStatus;

@Data
public class ReviewRequest {
    @NotNull
    private Long moderatorId;
    @NotNull
    private DonationStatus decision;
    private String reason;
}
