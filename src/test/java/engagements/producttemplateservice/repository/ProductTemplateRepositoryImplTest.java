package engagements.producttemplateservice.repository;

import engagements.producttemplateservice.domain.ProductTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTemplateRepositoryImplTest {

    private ProductTemplateRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ProductTemplateRepositoryImpl();
        repository.load();
    }

    @Test
    void loadsAllProductTemplatesFromCsv() {
        Collection<ProductTemplate> templates = repository.findAll();

        assertThat(templates).hasSize(3);
    }

    @Test
    void findsProductTemplateById() {
        List<ProductTemplate> templates = repository.findById(1L);

        assertThat(templates).hasSize(1);
        ProductTemplate template = templates.get(0);
        assertThat(template.getName()).isEqualTo("Basic Template");
        assertThat(template.getVersion()).isEqualTo("1.0");
        assertThat(template.getContent()).isEqualTo("{\"fields\":[\"title\",\"description\"]}");
    }

    @Test
    void returnsEmptyListWhenIdNotFound() {
        List<ProductTemplate> templates = repository.findById(999L);

        assertThat(templates).isEmpty();
    }

    @Test
    void returnsAllVersionsForAGivenId() {
        repository.save(new ProductTemplate(1L, "Basic Template", "3.0", "{}"));

        List<ProductTemplate> templates = repository.findById(1L);

        assertThat(templates).hasSize(2);
        assertThat(templates).extracting(ProductTemplate::getVersion)
                .containsExactlyInAnyOrder("1.0", "3.0");
    }

    @Test
    void findByIdAndVersionIsPresentForLoadedEntry() {
        assertThat(repository.findByIdAndVersion(1L, "1.0")).isPresent();
    }

    @Test
    void findByIdAndVersionIsEmptyForUnknownVersion() {
        assertThat(repository.findByIdAndVersion(1L, "9.9")).isEmpty();
    }

    @Test
    void saveAddsANewProductTemplate() {
        repository.save(new ProductTemplate(42L, "New Template", "1.0", "{\"fields\":[\"sku\"]}"));

        assertThat(repository.findAll()).hasSize(4);
        assertThat(repository.findByIdAndVersion(42L, "1.0")).isPresent();
    }
}
