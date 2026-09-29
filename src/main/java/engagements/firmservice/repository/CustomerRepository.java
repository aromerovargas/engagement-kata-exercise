package engagements.firmservice.repository;

import engagements.firmservice.domain.Customer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(Long id);

    List<Customer> findByFirmId(Long firmId);

    Collection<Customer> findAll();

    void save(Customer customer);
}
