package engagements.producttemplateservice.services;

import engagements.producttemplateservice.domain.ProductTemplate;
import engagements.producttemplateservice.repository.ProductTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddNewProductTemplateVersionTest {

    @Mock
    private ProductTemplateRepository productTemplateRepository;

    @Mock
    private MessageChannel updatedProductTemplateQueue;

    private AddNewProductTemplateVersion addNewProductTemplateVersion;

    @BeforeEach
    void setUp() {
        addNewProductTemplateVersion = new AddNewProductTemplateVersionImpl(
                productTemplateRepository, updatedProductTemplateQueue);
    }

    @Test
    void addsNewVersionAndPublishesUpdateWhenNoneExists() {
        when(productTemplateRepository.findByIdAndVersion(100L, "1.0")).thenReturn(Optional.empty());

        boolean created = addNewProductTemplateVersion.addVersion(100L, "Product 100", "1.0", "{\"fields\":[]}");

        assertThat(created).isTrue();

        ArgumentCaptor<ProductTemplate> captor = ArgumentCaptor.forClass(ProductTemplate.class);
        verify(productTemplateRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(100L);
        assertThat(captor.getValue().getVersion()).isEqualTo("1.0");
        assertThat(captor.getValue().getContent()).isEqualTo("{\"fields\":[]}");

        verify(updatedProductTemplateQueue).send(any(Message.class));
    }

    @Test
    void failsWhenAddingAnAlreadyExistingIdAndVersionCombinationWithDifferentContent() {
        when(productTemplateRepository.findByIdAndVersion(200L, "1.0"))
                .thenReturn(Optional.of(new ProductTemplate(200L, "Product 200", "1.0", "{}")));

        assertThatThrownBy(() -> addNewProductTemplateVersion.addVersion(200L, "Product 200", "1.0", "{\"changed\":true}"))
                .isInstanceOf(ProductTemplateVersionAlreadyExistsException.class);
    }

    @Test
    void retryingWithIdenticalContentIsIdempotentAndDoesNotFail() {
        when(productTemplateRepository.findByIdAndVersion(400L, "1.0"))
                .thenReturn(Optional.empty(), Optional.of(new ProductTemplate(400L, "Product 400", "1.0", "{}")));

        boolean firstCallCreatedVersion = addNewProductTemplateVersion.addVersion(400L, "Product 400", "1.0", "{}");
        boolean retryCreatedVersion = addNewProductTemplateVersion.addVersion(400L, "Product 400", "1.0", "{}");

        assertThat(firstCallCreatedVersion).isTrue();
        assertThat(retryCreatedVersion).isFalse();
        verify(productTemplateRepository, times(1)).save(any(ProductTemplate.class));
    }

    @Test
    void allowsSameIdWithDifferentVersion() {
        when(productTemplateRepository.findByIdAndVersion(300L, "1.0")).thenReturn(Optional.empty());
        when(productTemplateRepository.findByIdAndVersion(300L, "2.0")).thenReturn(Optional.empty());

        addNewProductTemplateVersion.addVersion(300L, "Product 300", "1.0", "{}");
        addNewProductTemplateVersion.addVersion(300L, "Product 300", "2.0", "{}");

        ArgumentCaptor<ProductTemplate> captor = ArgumentCaptor.forClass(ProductTemplate.class);
        verify(productTemplateRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(ProductTemplate::getVersion)
                .containsExactly("1.0", "2.0");
    }
}
