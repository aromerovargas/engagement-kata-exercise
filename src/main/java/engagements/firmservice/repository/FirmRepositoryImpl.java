package engagements.firmservice.repository;

import engagements.firmservice.domain.Firm;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;

@Repository
public class FirmRepositoryImpl implements FirmRepository {

    private static final String CSV_PATH = "firms.csv";

    private final Hashtable<Long, Firm> firms = new Hashtable<>();

    @PostConstruct
    void load() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                Long id = Long.parseLong(fields[0].trim());
                String name = fields[1].trim();
                firms.put(id, new Firm(id, name));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load firms from " + CSV_PATH, e);
        }
    }

    @Override
    public Optional<Firm> findById(Long id) {
        return Optional.ofNullable(firms.get(id));
    }

    @Override
    public Collection<Firm> findAll() {
        return Collections.unmodifiableCollection(firms.values());
    }

    @Override
    public void save(Firm firm) {
        firms.put(firm.getId(), firm);
    }
}
