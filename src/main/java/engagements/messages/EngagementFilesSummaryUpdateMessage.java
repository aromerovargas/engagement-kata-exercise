package engagements.messages;

import java.util.List;

public record EngagementFilesSummaryUpdateMessage(
        Long firmId,
        Long productTemplateId,
        String currentVersion,
        String newAvailableVersion,
        List<Long> customerIds) {
}
