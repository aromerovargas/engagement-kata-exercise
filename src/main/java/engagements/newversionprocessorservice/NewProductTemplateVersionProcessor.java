package engagements.newversionprocessorservice;

import engagements.firmservice.domain.Customer;
import engagements.firmservice.domain.Firm;
import engagements.firmservice.services.FindCustomersByProductTemplate;
import engagements.messages.EngagementFilesSummaryUpdateMessage;
import engagements.messages.ProductTemplateUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NewProductTemplateVersionProcessor {

    private static final Logger log = LoggerFactory.getLogger(NewProductTemplateVersionProcessor.class);

    private final FindCustomersByProductTemplate findCustomersByProductTemplate;
    private final MessageChannel updateEngagementFilesSummaryQueue;

    public NewProductTemplateVersionProcessor(
            FindCustomersByProductTemplate findCustomersByProductTemplate,
            @Qualifier("update_engagement_files_summary_queue") MessageChannel updateEngagementFilesSummaryQueue) {
        this.findCustomersByProductTemplate = findCustomersByProductTemplate;
        this.updateEngagementFilesSummaryQueue = updateEngagementFilesSummaryQueue;
    }

    @ServiceActivator(inputChannel = "updated_product_template_queue")
    public void handle(ProductTemplateUpdatedEvent event) {
        log.info("Received product template update event: productTemplateId={}, newVersion={}",
                event.productTemplateId(), event.newVersion());

        Map<Firm, Map<String, List<Customer>>> customersByFirm =
                findCustomersByProductTemplate.findByProductTemplateId(event.productTemplateId());

        for (Map.Entry<Firm, Map<String, List<Customer>>> firmEntry : customersByFirm.entrySet()) {
            Firm firm = firmEntry.getKey();
            for (Map.Entry<String, List<Customer>> versionEntry : firmEntry.getValue().entrySet()) {
                publishEngagementFilesSummaryUpdate(
                        firm, event.productTemplateId(), versionEntry.getKey(), event.newVersion(), versionEntry.getValue());
            }
        }
    }

    private void publishEngagementFilesSummaryUpdate(
            Firm firm,
            Long productTemplateId,
            String currentVersion,
            String newAvailableVersion,
            List<Customer> customers) {
        List<Long> customerIds = customers.stream().map(Customer::getId).toList();

        EngagementFilesSummaryUpdateMessage payload = new EngagementFilesSummaryUpdateMessage(
                firm.getId(), productTemplateId, currentVersion, newAvailableVersion, customerIds);
        updateEngagementFilesSummaryQueue.send(MessageBuilder.withPayload(payload).build());

        log.info("Published engagement files summary update message: firmId={}, productTemplateId={}, currentVersion={}",
                firm.getId(), productTemplateId, currentVersion);
    }
}
