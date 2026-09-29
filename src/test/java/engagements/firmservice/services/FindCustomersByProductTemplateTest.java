package engagements.firmservice.services;

import engagements.firmservice.domain.Customer;
import engagements.firmservice.domain.CustomerProductTemplates;
import engagements.firmservice.domain.Firm;
import engagements.firmservice.repository.CustomerProductTemplatesRepository;
import engagements.firmservice.repository.CustomerRepository;
import engagements.firmservice.repository.FirmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCustomersByProductTemplateTest {

    @Mock
    private FirmRepository firmRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProductTemplatesRepository customerProductTemplatesRepository;

    private FindCustomersByProductTemplate findCustomersByProductTemplate;

    private final Firm firmOne = new Firm(1L, "Firm One");
    private final Firm firmTwo = new Firm(2L, "Firm Two");
    private final Firm firmThree = new Firm(3L, "Firm Three");

    private final Customer johnDoe = new Customer(10L, 1L, "John Doe");
    private final Customer aliceWilliams = new Customer(30L, 3L, "Alice Williams");

    @BeforeEach
    void setUp() {
        findCustomersByProductTemplate = new FindCustomersByProductTemplateImpl(
                firmRepository, customerRepository, customerProductTemplatesRepository);

        when(firmRepository.findAll()).thenReturn(List.of(firmOne, firmTwo, firmThree));
        when(customerProductTemplatesRepository.findByFirmId(1L))
                .thenReturn(List.of(new CustomerProductTemplates(1L, 10L, 1L, "1.0")));
        when(customerProductTemplatesRepository.findByFirmId(2L)).thenReturn(List.of());
        when(customerProductTemplatesRepository.findByFirmId(3L))
                .thenReturn(List.of(new CustomerProductTemplates(3L, 30L, 1L, "1.0")));
    }

    @Test
    void groupsCustomersByFirmThenByProductTemplateVersion() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(johnDoe));
        when(customerRepository.findById(30L)).thenReturn(Optional.of(aliceWilliams));

        Map<Firm, Map<String, List<Customer>>> result =
                findCustomersByProductTemplate.findByProductTemplateId(1L);

        assertThat(result).hasSize(2);

        Firm resultFirmOne = result.keySet().stream().filter(firm -> firm.getId().equals(1L)).findFirst().orElseThrow();
        Map<String, List<Customer>> firmOneGroups = result.get(resultFirmOne);
        assertThat(firmOneGroups).containsKey("1.0");
        assertThat(firmOneGroups.get("1.0"))
                .extracting(Customer::getName)
                .containsExactly("John Doe");

        Firm resultFirmThree = result.keySet().stream().filter(firm -> firm.getId().equals(3L)).findFirst().orElseThrow();
        Map<String, List<Customer>> firmThreeGroups = result.get(resultFirmThree);
        assertThat(firmThreeGroups.get("1.0"))
                .extracting(Customer::getName)
                .containsExactly("Alice Williams");
    }

    @Test
    void returnsEmptyMapWhenNoMatchesExist() {
        Map<Firm, Map<String, List<Customer>>> result =
                findCustomersByProductTemplate.findByProductTemplateId(999L);

        assertThat(result).isEmpty();
    }
}
