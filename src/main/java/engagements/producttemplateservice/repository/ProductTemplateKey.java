package engagements.producttemplateservice.repository;

import java.util.Objects;

record ProductTemplateKey(Long id, String version) {

    ProductTemplateKey {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(version, "version must not be null");
    }
}
