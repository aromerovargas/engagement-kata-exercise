package engagements.producttemplateservice.services;

import engagements.producttemplateservice.domain.ProductTemplate;
import engagements.producttemplateservice.domain.ProductTemplateDiff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetDiffForProductTemplateImplTest {

    private GetDiffForProductTemplate getDiffForProductTemplate;

    @BeforeEach
    void setUp() {
        getDiffForProductTemplate = new GetDiffForProductTemplateImpl();
    }

    @Test
    void returnsNoDifferencesWhenFieldsAreIdentical() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "{\"fieldA\":1,\"fieldB\":2}");
        ProductTemplate targetVersion = new ProductTemplate(1L, "Product 1", "2.0", "{\"fieldA\":3,\"fieldB\":4}");

        ProductTemplateDiff diff = getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion);

        assertThat(diff.newFields()).isEmpty();
        assertThat(diff.removedFields()).isEmpty();
    }

    @Test
    void detectsNewFieldsAddedInTargetVersion() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "{\"fieldA\":1}");
        ProductTemplate targetVersion = new ProductTemplate(1L, "Product 1", "2.0", "{\"fieldA\":1,\"fieldB\":2}");

        ProductTemplateDiff diff = getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion);

        assertThat(diff.newFields()).containsExactly("fieldB");
        assertThat(diff.removedFields()).isEmpty();
    }

    @Test
    void detectsFieldsRemovedInTargetVersion() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "{\"fieldA\":1,\"fieldB\":2}");
        ProductTemplate targetVersion = new ProductTemplate(1L, "Product 1", "2.0", "{\"fieldA\":1}");

        ProductTemplateDiff diff = getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion);

        assertThat(diff.newFields()).isEmpty();
        assertThat(diff.removedFields()).containsExactly("fieldB");
    }

    @Test
    void detectsBothNewAndRemovedFields() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "{\"fieldA\":1,\"fieldB\":2}");
        ProductTemplate targetVersion = new ProductTemplate(1L, "Product 1", "2.0", "{\"fieldA\":1,\"fieldC\":3}");

        ProductTemplateDiff diff = getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion);

        assertThat(diff.newFields()).containsExactly("fieldC");
        assertThat(diff.removedFields()).containsExactly("fieldB");
    }

    @Test
    void throwsWhenSourceContentIsInvalidJson() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "not-json");
        ProductTemplate targetVersion = new ProductTemplate(1L, "Product 1", "2.0", "{\"fieldA\":1}");

        assertThatThrownBy(() -> getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid JSON content for product template 1");
    }

    @Test
    void throwsWhenTargetContentIsInvalidJson() {
        ProductTemplate sourceVersion = new ProductTemplate(1L, "Product 1", "1.0", "{\"fieldA\":1}");
        ProductTemplate targetVersion = new ProductTemplate(2L, "Product 1", "2.0", "not-json");

        assertThatThrownBy(() -> getDiffForProductTemplate.getDiffBetweenVersions(sourceVersion, targetVersion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid JSON content for product template 2");
    }
}
