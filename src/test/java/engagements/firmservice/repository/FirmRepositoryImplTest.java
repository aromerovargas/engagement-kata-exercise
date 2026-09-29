package engagements.firmservice.repository;

import engagements.firmservice.domain.Firm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class FirmRepositoryImplTest {

    private FirmRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new FirmRepositoryImpl();
        repository.load();
    }

    @Test
    void loadsAllFirmsFromCsv() {
        Collection<Firm> firms = repository.findAll();

        assertThat(firms).hasSize(3);
    }

    @Test
    void findsFirmById() {
        assertThat(repository.findById(1L))
                .isPresent()
                .get()
                .extracting(Firm::getName)
                .isEqualTo("Acme Legal");
    }

    @Test
    void returnsEmptyWhenIdNotFound() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void saveAddsANewFirm() {
        repository.save(new Firm(42L, "New Firm"));

        assertThat(repository.findAll()).hasSize(4);
        assertThat(repository.findById(42L)).isPresent();
    }
}
