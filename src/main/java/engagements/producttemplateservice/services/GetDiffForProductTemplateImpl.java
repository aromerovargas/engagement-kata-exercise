package engagements.producttemplateservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import engagements.producttemplateservice.domain.ProductTemplate;
import engagements.producttemplateservice.domain.ProductTemplateDiff;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetDiffForProductTemplateImpl implements GetDiffForProductTemplate {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductTemplateDiff getDiffBetweenVersions(ProductTemplate sourceVersion, ProductTemplate targetVersion) {
        List<String> sourceFields = fieldNamesOf(sourceVersion);
        List<String> targetFields = fieldNamesOf(targetVersion);

        List<String> newFields = targetFields.stream()
                .filter(field -> !sourceFields.contains(field))
                .toList();

        List<String> removedFields = sourceFields.stream()
                .filter(field -> !targetFields.contains(field))
                .toList();

        return new ProductTemplateDiff(newFields, removedFields);
    }

    private List<String> fieldNamesOf(ProductTemplate productTemplate) {
        try {
            JsonNode contentNode = objectMapper.readTree(productTemplate.getContent());
            List<String> fieldNames = new ArrayList<>();
            contentNode.fieldNames().forEachRemaining(fieldNames::add);
            return fieldNames;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON content for product template " + productTemplate.getId(), e);
        }
    }
}
