package engagements.firmservice.repository;

import engagements.firmservice.domain.CustomerProductTemplates;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerProductTemplatesRepositoryImpl implements CustomerProductTemplatesRepository {

    private static final String CSV_PATH = "customer-product-templates.csv";

    private final Hashtable<CustomerProductTemplateKey, CustomerProductTemplates> customerProductTemplates = new Hashtable<>();

    @PostConstruct
    void load() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                Long firmId = Long.parseLong(fields[0].trim());
                Long customerId = Long.parseLong(fields[1].trim());
                Long productTemplateId = Long.parseLong(fields[2].trim());
                String currentVersion = fields[3].trim();
                customerProductTemplates.put(
                        new CustomerProductTemplateKey(firmId, customerId, productTemplateId),
                        new CustomerProductTemplates(firmId, customerId, productTemplateId, currentVersion));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load customer product templates from " + CSV_PATH, e);
        }
    }

    @Override
    public List<CustomerProductTemplates> findByCustomerId(Long customerId) {
        return customerProductTemplates.values().stream()
                .filter(entry -> entry.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<CustomerProductTemplates> findByFirmId(Long firmId) {
        return customerProductTemplates.values().stream()
                .filter(entry -> entry.getFirmId().equals(firmId))
                .toList();
    }

    @Override
    public Optional<CustomerProductTemplates> findByFirmIdAndCustomerIdAndProductTemplateId(
            Long firmId, Long customerId, Long productTemplateId) {
        return Optional.ofNullable(
                customerProductTemplates.get(new CustomerProductTemplateKey(firmId, customerId, productTemplateId)));
    }

    @Override
    public Collection<CustomerProductTemplates> findAll() {
        return Collections.unmodifiableCollection(customerProductTemplates.values());
    }

    @Override
    public void save(CustomerProductTemplates customerProductTemplates) {
        this.customerProductTemplates.put(
                new CustomerProductTemplateKey(
                        customerProductTemplates.getFirmId(),
                        customerProductTemplates.getCustomerId(),
                        customerProductTemplates.getProductTemplateId()),
                customerProductTemplates);
    }
}
