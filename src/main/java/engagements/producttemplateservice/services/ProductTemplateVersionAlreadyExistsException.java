package engagements.producttemplateservice.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductTemplateVersionAlreadyExistsException extends RuntimeException {

    public ProductTemplateVersionAlreadyExistsException(Long productTemplateId, String version) {
        super("Product template with id %d and version %s already exists".formatted(productTemplateId, version));
    }
}
