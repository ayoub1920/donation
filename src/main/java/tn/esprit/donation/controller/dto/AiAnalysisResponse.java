package tn.esprit.donation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {
    private String improvedText;
    private String category;
    private String categoryLabel;
    private int impactScore;
    private List<String> suggestions;
}
