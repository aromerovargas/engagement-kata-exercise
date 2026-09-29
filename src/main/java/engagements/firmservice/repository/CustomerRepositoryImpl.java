package engagements.firmservice.repository;

import engagements.firmservice.domain.Customer;
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
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final String CSV_PATH = "customers.csv";

    private final Hashtable<Long, Customer> customers = new Hashtable<>();

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
                Long id = Long.parseLong(fields[0].trim());
                Long firmId = Long.parseLong(fields[1].trim());
                String name = fields[2].trim();
                customers.put(id, new Customer(id, firmId, name));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load customers from " + CSV_PATH, e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findByFirmId(Long firmId) {
        return customers.values().stream()
                .filter(customer -> customer.getFirmId().equals(firmId))
                .toList();
    }

    @Override
    public Collection<Customer> findAll() {
        return Collections.unmodifiableCollection(customers.values());
    }

    @Override
    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }
}
