package engagements.firmservice.repository;

import engagements.firmservice.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryImplTest {

    private CustomerRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerRepositoryImpl();
        repository.load();
    }

    @Test
    void loadsAllCustomersFromCsv() {
        Collection<Customer> customers = repository.findAll();

        assertThat(customers).hasSize(4);
    }

    @Test
    void findsCustomerById() {
        assertThat(repository.findById(1L))
                .isPresent()
                .get()
                .extracting(Customer::getName)
                .isEqualTo("John Doe");
    }

    @Test
    void returnsEmptyWhenIdNotFound() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void findsCustomersByFirmId() {
        List<Customer> customers = repository.findByFirmId(1L);

        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(Customer::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    void saveAddsANewCustomer() {
        repository.save(new Customer(42L, 1L, "New Customer"));

        assertThat(repository.findAll()).hasSize(5);
        assertThat(repository.findById(42L)).isPresent();
    }
}
