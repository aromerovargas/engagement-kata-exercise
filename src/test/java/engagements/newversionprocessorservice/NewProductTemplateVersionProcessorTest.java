package engagements.newversionprocessorservice;

import engagements.firmservice.domain.Customer;
import engagements.firmservice.domain.Firm;
import engagements.firmservice.services.FindCustomersByProductTemplate;
import engagements.messages.EngagementFilesSummaryUpdateMessage;
import engagements.messages.ProductTemplateUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewProductTemplateVersionProcessorTest {

    @Mock
    private FindCustomersByProductTemplate findCustomersByProductTemplate;

    @Mock
    private MessageChannel updateEngagementFilesSummaryQueue;

    private NewProductTemplateVersionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new NewProductTemplateVersionProcessor(findCustomersByProductTemplate, updateEngagementFilesSummaryQueue);
    }

    @Test
    void publishesOneSummaryUpdatePerFirmAndVersionCombination() {
        Firm firm = new Firm(10L, "Firm 10");
        Customer customer1 = new Customer(1L, 10L, "Customer 1");
        Customer customer2 = new Customer(2L, 10L, "Customer 2");

        when(findCustomersByProductTemplate.findByProductTemplateId(100L))
                .thenReturn(Map.of(firm, Map.of("1.0", List.of(customer1, customer2))));

        processor.handle(new ProductTemplateUpdatedEvent(100L, "2.0"));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(updateEngagementFilesSummaryQueue).send(messageCaptor.capture());

        EngagementFilesSummaryUpdateMessage payload =
                (EngagementFilesSummaryUpdateMessage) messageCaptor.getValue().getPayload();
        assertThat(payload.firmId()).isEqualTo(10L);
        assertThat(payload.productTemplateId()).isEqualTo(100L);
        assertThat(payload.currentVersion()).isEqualTo("1.0");
        assertThat(payload.newAvailableVersion()).isEqualTo("2.0");
        assertThat(payload.customerIds()).containsExactly(1L, 2L);
    }

    @Test
    void publishesASeparateMessagePerVersionWithinTheSameFirm() {
        Firm firm = new Firm(10L, "Firm 10");
        Customer customerOnV1 = new Customer(1L, 10L, "Customer 1");
        Customer customerOnV2 = new Customer(2L, 10L, "Customer 2");

        when(findCustomersByProductTemplate.findByProductTemplateId(100L))
                .thenReturn(Map.of(firm, Map.of(
                        "1.0", List.of(customerOnV1),
                        "1.1", List.of(customerOnV2))));

        processor.handle(new ProductTemplateUpdatedEvent(100L, "2.0"));

        verify(updateEngagementFilesSummaryQueue, times(2)).send(any());
    }

    @Test
    void publishesAMessagePerFirmWhenMultipleFirmsHaveCustomers() {
        Firm firm1 = new Firm(10L, "Firm 10");
        Firm firm2 = new Firm(20L, "Firm 20");
        Customer customer1 = new Customer(1L, 10L, "Customer 1");
        Customer customer2 = new Customer(2L, 20L, "Customer 2");

        when(findCustomersByProductTemplate.findByProductTemplateId(100L))
                .thenReturn(Map.of(
                        firm1, Map.of("1.0", List.of(customer1)),
                        firm2, Map.of("1.0", List.of(customer2))));

        processor.handle(new ProductTemplateUpdatedEvent(100L, "2.0"));

        verify(updateEngagementFilesSummaryQueue, times(2)).send(any());
    }

    @Test
    void doesNothingWhenNoCustomersUseTheProductTemplate() {
        when(findCustomersByProductTemplate.findByProductTemplateId(100L)).thenReturn(Map.of());

        processor.handle(new ProductTemplateUpdatedEvent(100L, "2.0"));

        verify(updateEngagementFilesSummaryQueue, never()).send(any());
    }
}
