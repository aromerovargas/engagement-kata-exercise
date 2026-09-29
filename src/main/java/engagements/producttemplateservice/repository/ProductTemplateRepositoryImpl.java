package engagements.producttemplateservice.repository;

import engagements.producttemplateservice.domain.ProductTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductTemplateRepositoryImpl implements ProductTemplateRepository {

    private static final String CSV_PATH = "product-templates.csv";

    private final Hashtable<ProductTemplateKey, ProductTemplate> productTemplates = new Hashtable<>();

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
                List<String> fields = parseCsvLine(line);
                Long id = Long.parseLong(fields.get(0).trim());
                String name = fields.get(1).trim();
                String version = fields.get(2).trim();
                String content = fields.get(3);
                productTemplates.put(new ProductTemplateKey(id, version), new ProductTemplate(id, name, version, content));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load product templates from " + CSV_PATH, e);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    @Override
    public List<ProductTemplate> findById(Long id) {
        return productTemplates.values().stream()
                .filter(productTemplate -> productTemplate.getId().equals(id))
                .toList();
    }

    @Override
    public Collection<ProductTemplate> findAll() {
        return Collections.unmodifiableCollection(productTemplates.values());
    }

    @Override
    public Optional<ProductTemplate> findByIdAndVersion(Long productTemplateId, String version) {
        return Optional.ofNullable(productTemplates.get(new ProductTemplateKey(productTemplateId, version)));
    }

    @Override
    public void save(ProductTemplate productTemplate) {
        productTemplates.put(new ProductTemplateKey(productTemplate.getId(), productTemplate.getVersion()), productTemplate);
    }
}
