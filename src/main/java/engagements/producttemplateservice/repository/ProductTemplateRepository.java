package engagements.producttemplateservice.repository;

import engagements.producttemplateservice.domain.ProductTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductTemplateRepository {

    List<ProductTemplate> findById(Long id);

    Collection<ProductTemplate> findAll();

    Optional<ProductTemplate> findByIdAndVersion(Long productTemplateId, String version);

    void save(ProductTemplate productTemplate);
}
