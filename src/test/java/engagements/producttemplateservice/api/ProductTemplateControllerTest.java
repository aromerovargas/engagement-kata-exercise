package engagements.producttemplateservice.api;

import engagements.producttemplateservice.services.AddNewProductTemplateVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductTemplateControllerTest {

    @Mock
    private AddNewProductTemplateVersion addNewProductTemplateVersion;

    private ProductTemplateController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductTemplateController(addNewProductTemplateVersion);
    }

    @Test
    void returnsCreatedWhenANewVersionIsAdded() {
        when(addNewProductTemplateVersion.addVersion(100L, "Product 100", "1.0", "{}")).thenReturn(true);

        ResponseEntity<Void> response = controller.addVersion(100L, new ProductTemplateVersionRequest("Product 100", "1.0", "{}"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(addNewProductTemplateVersion).addVersion(100L, "Product 100", "1.0", "{}");
    }

    @Test
    void returnsOkWhenTheVersionAlreadyExistedIdempotently() {
        when(addNewProductTemplateVersion.addVersion(100L, "Product 100", "1.0", "{}")).thenReturn(false);

        ResponseEntity<Void> response = controller.addVersion(100L, new ProductTemplateVersionRequest("Product 100", "1.0", "{}"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
