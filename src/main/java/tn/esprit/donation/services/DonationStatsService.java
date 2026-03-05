package tn.esprit.donation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.donation.repository.DonationRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DonationStatsService {

    private final DonationRepository donationRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalDonations", donationRepository.count());
        stats.put("totalQuantity", donationRepository.totalQuantity());

        // Count by status: { "PENDING": 5, "ACCEPTED": 12, "REJECTED": 2 }
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Object[] row : donationRepository.countByStatus()) {
            byStatus.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byStatus", byStatus);

        // Count by type: { "VETEMENT": 10, "JEU": 7 }
        Map<String, Long> byType = new LinkedHashMap<>();
        for (Object[] row : donationRepository.countByType()) {
            byType.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byType", byType);

        // Count by condition: { "NEUF": 3, "BON_ETAT": 8, "ACCEPTABLE": 6 }
        Map<String, Long> byCondition = new LinkedHashMap<>();
        for (Object[] row : donationRepository.countByCondition()) {
            byCondition.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byCondition", byCondition);

        // Count by month: { "2026-03": 5, "2026-02": 12 }
        Map<String, Long> byMonth = new LinkedHashMap<>();
        for (Object[] row : donationRepository.countByMonth()) {
            byMonth.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byMonth", byMonth);

        return stats;
    }
}
