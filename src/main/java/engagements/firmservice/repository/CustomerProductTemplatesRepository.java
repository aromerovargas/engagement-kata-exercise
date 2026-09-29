package engagements.firmservice.repository;

import engagements.firmservice.domain.CustomerProductTemplates;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerProductTemplatesRepository {

    List<CustomerProductTemplates> findByCustomerId(Long customerId);

    List<CustomerProductTemplates> findByFirmId(Long firmId);

    Optional<CustomerProductTemplates> findByFirmIdAndCustomerIdAndProductTemplateId(
            Long firmId, Long customerId, Long productTemplateId);

    Collection<CustomerProductTemplates> findAll();

    void save(CustomerProductTemplates customerProductTemplates);
}
