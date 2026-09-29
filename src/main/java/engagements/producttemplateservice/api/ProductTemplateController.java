package engagements.producttemplateservice.api;

import engagements.producttemplateservice.services.AddNewProductTemplateVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product-templates")
public class ProductTemplateController {

    private static final Logger log = LoggerFactory.getLogger(ProductTemplateController.class);

    private final AddNewProductTemplateVersion addNewProductTemplateVersion;

    public ProductTemplateController(AddNewProductTemplateVersion addNewProductTemplateVersion) {
        this.addNewProductTemplateVersion = addNewProductTemplateVersion;
    }

    @PostMapping("/{productTemplateId}/versions")
    public ResponseEntity<Void> addVersion(@PathVariable Long productTemplateId, @RequestBody ProductTemplateVersionRequest request) {
        log.info("Received request to add product template version: productTemplateId={}, name={}, version={}, content={}",
                productTemplateId, request.name(), request.version(), request.content());

        boolean versionCreated = addNewProductTemplateVersion.addVersion(
                productTemplateId, request.name(), request.version(), request.content());

        HttpStatus status = versionCreated ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).build();
    }
}
