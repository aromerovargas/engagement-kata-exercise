package engagements.producttemplateservice.services;

import engagements.producttemplateservice.domain.ProductTemplate;
import engagements.producttemplateservice.domain.ProductTemplateDiff;

public interface GetDiffForProductTemplate {

    ProductTemplateDiff getDiffBetweenVersions(ProductTemplate sourceVersion, ProductTemplate targetVersion);
}
