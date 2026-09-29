package engagements.firmservice.repository;

import engagements.firmservice.domain.Firm;

import java.util.Collection;
import java.util.Optional;

public interface FirmRepository {

    Optional<Firm> findById(Long id);

    Collection<Firm> findAll();

    void save(Firm firm);
}
