package engagements.producttemplateservice.domain;

import java.util.List;

public record ProductTemplateDiff(List<String> newFields, List<String> removedFields) {
}
