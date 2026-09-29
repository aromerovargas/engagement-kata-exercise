package engagements.firmservice.repository;

import engagements.firmservice.domain.CustomerProductTemplates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerProductTemplatesRepositoryImplTest {

    private CustomerProductTemplatesRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerProductTemplatesRepositoryImpl();
        repository.load();
    }

    @Test
    void loadsAllCustomerProductTemplatesFromCsv() {
        Collection<CustomerProductTemplates> entries = repository.findAll();

        assertThat(entries).hasSize(4);
    }

    @Test
    void findsByCustomerId() {
        List<CustomerProductTemplates> entries = repository.findByCustomerId(1L);

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getCurrentVersion()).isEqualTo("1.0");
    }

    @Test
    void findsByFirmId() {
        List<CustomerProductTemplates> entries = repository.findByFirmId(1L);

        assertThat(entries).hasSize(2);
    }

    @Test
    void findsByFirmIdAndCustomerIdAndProductTemplateId() {
        assertThat(repository.findByFirmIdAndCustomerIdAndProductTemplateId(1L, 1L, 1L)).isPresent();
    }

    @Test
    void returnsEmptyWhenCombinationNotFound() {
        assertThat(repository.findByFirmIdAndCustomerIdAndProductTemplateId(1L, 1L, 999L)).isEmpty();
    }

    @Test
    void saveAddsANewEntry() {
        repository.save(new CustomerProductTemplates(1L, 1L, 5L, "1.0"));

        assertThat(repository.findAll()).hasSize(5);
        assertThat(repository.findByFirmIdAndCustomerIdAndProductTemplateId(1L, 1L, 5L)).isPresent();
    }
}
