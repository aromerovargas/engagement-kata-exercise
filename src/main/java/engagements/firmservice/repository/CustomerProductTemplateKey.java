package engagements.firmservice.repository;

import java.util.Objects;

record CustomerProductTemplateKey(Long firmId, Long customerId, Long productTemplateId) {

    CustomerProductTemplateKey {
        Objects.requireNonNull(firmId, "firmId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(productTemplateId, "productTemplateId must not be null");
    }
}
