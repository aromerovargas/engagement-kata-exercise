package engagements.summaryservice;

import engagements.messages.EngagementFilesSummaryUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

@Service
public class EngagementFilesSummaryUpdateProcessor {

    private static final Logger log = LoggerFactory.getLogger(EngagementFilesSummaryUpdateProcessor.class);

    @ServiceActivator(
            inputChannel = "update_engagement_files_summary_queue",
            poller = @Poller(fixedDelay = "1000", taskExecutor = "summaryTaskExecutor"))
    public void handle(EngagementFilesSummaryUpdateMessage message) {
        log.info("Received engagement files summary update message: firmId={}, productTemplateId={}, currentVersion={}, newAvailableVersion={}, customerIds={}",
                message.firmId(), message.productTemplateId(), message.currentVersion(), message.newAvailableVersion(), message.customerIds());
    }
}
