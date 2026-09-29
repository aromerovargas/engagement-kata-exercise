package engagements.producttemplateservice.services;

import engagements.messages.ProductTemplateUpdatedEvent;
import engagements.producttemplateservice.domain.ProductTemplate;
import engagements.producttemplateservice.repository.ProductTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddNewProductTemplateVersionImpl implements AddNewProductTemplateVersion {

    private static final Logger log = LoggerFactory.getLogger(AddNewProductTemplateVersionImpl.class);

    private final ProductTemplateRepository productTemplateRepository;
    private final MessageChannel updatedProductTemplateQueue;

    public AddNewProductTemplateVersionImpl(
            ProductTemplateRepository productTemplateRepository,
            @Qualifier("updated_product_template_queue") MessageChannel updatedProductTemplateQueue) {
        this.productTemplateRepository = productTemplateRepository;
        this.updatedProductTemplateQueue = updatedProductTemplateQueue;
    }

    @Override
    public boolean addVersion(Long productTemplateId, String name, String version, String content) {
        Optional<ProductTemplate> existingVersion = productTemplateRepository.findByIdAndVersion(productTemplateId, version);

        if (existingVersion.isPresent()) {
            boolean isIdempotentRetry = existingVersion.get().getContent().equals(content);
            if (isIdempotentRetry) {
                return false;
            }
            throw new ProductTemplateVersionAlreadyExistsException(productTemplateId, version);
        }

        productTemplateRepository.save(new ProductTemplate(productTemplateId, name, version, content));
        publishVersionUpdatedMessage(productTemplateId, version);
        log.info("Product template registered and update message published: productTemplateId={}, version={}",
                productTemplateId, version);
        return true;
    }

    private void publishVersionUpdatedMessage(Long productTemplateId, String newVersion) {
        ProductTemplateUpdatedEvent payload = new ProductTemplateUpdatedEvent(productTemplateId, newVersion);
        updatedProductTemplateQueue.send(MessageBuilder.withPayload(payload).build());
    }
}
