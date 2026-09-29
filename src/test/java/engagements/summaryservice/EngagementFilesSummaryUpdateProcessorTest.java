package engagements.summaryservice;

import engagements.messages.EngagementFilesSummaryUpdateMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class EngagementFilesSummaryUpdateProcessorTest {

    private final EngagementFilesSummaryUpdateProcessor processor = new EngagementFilesSummaryUpdateProcessor();

    @Test
    void handlesMessageWithoutThrowing() {
        EngagementFilesSummaryUpdateMessage message =
                new EngagementFilesSummaryUpdateMessage(10L, 100L, "1.0", "2.0", List.of(1L, 2L));

        assertThatCode(() -> processor.handle(message)).doesNotThrowAnyException();
    }

    @Test
    void handlesMessageWithEmptyCustomerIdsWithoutThrowing() {
        EngagementFilesSummaryUpdateMessage message =
                new EngagementFilesSummaryUpdateMessage(10L, 100L, "1.0", "2.0", List.of());

        assertThatCode(() -> processor.handle(message)).doesNotThrowAnyException();
    }
}
