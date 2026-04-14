package tn.esprit.donation.controller.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiSuggestionRequest {
    private Long userId;
    private List<String> previousDonationItems;
}
