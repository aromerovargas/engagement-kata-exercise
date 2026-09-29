package engagements.firmservice.services;

import engagements.firmservice.domain.Customer;
import engagements.firmservice.domain.CustomerProductTemplates;
import engagements.firmservice.domain.Firm;
import engagements.firmservice.repository.CustomerProductTemplatesRepository;
import engagements.firmservice.repository.CustomerRepository;
import engagements.firmservice.repository.FirmRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class
FindCustomersByProductTemplateImpl implements FindCustomersByProductTemplate {

    private final FirmRepository firmRepository;
    private final CustomerRepository customerRepository;
    private final CustomerProductTemplatesRepository customerProductTemplatesRepository;

    public FindCustomersByProductTemplateImpl(
            FirmRepository firmRepository,
            CustomerRepository customerRepository,
            CustomerProductTemplatesRepository customerProductTemplatesRepository) {
        this.firmRepository = firmRepository;
        this.customerRepository = customerRepository;
        this.customerProductTemplatesRepository = customerProductTemplatesRepository;
    }

    @Override
    public Map<Firm, Map<String, List<Customer>>> findByProductTemplateId(Long productTemplateId) {
        Map<Firm, Map<String, List<Customer>>> result = new LinkedHashMap<>();

        for (Firm firm : firmRepository.findAll()) {
            List<CustomerProductTemplates> matches = customerProductTemplatesRepository.findByFirmId(firm.getId()).stream()
                    .filter(entry -> entry.getProductTemplateId().equals(productTemplateId))
                    .toList();

            if (matches.isEmpty()) {
                continue;
            }

            Map<String, List<Customer>> customersByVersion = matches.stream()
                    .collect(Collectors.groupingBy(
                            CustomerProductTemplates::getCurrentVersion,
                            LinkedHashMap::new,
                            Collectors.mapping(this::resolveCustomer, Collectors.toList())));

            result.put(firm, customersByVersion);
        }

        return result;
    }

    private Customer resolveCustomer(CustomerProductTemplates entry) {
        return customerRepository.findById(entry.getCustomerId())
                .orElseThrow(() -> new IllegalStateException("Customer not found for id " + entry.getCustomerId()));
    }
}
