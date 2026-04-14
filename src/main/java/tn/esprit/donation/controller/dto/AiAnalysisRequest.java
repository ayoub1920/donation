package tn.esprit.donation.controller.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiAnalysisRequest {
    private String itemName;
    private String description;
    private String condition;
    private Integer quantity;
    private String type;
    private List<String> previousDonationItems;
}
